package com.danh3945.simplespeeddial.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.danh3945.simplespeeddial.freeVersionUtilities.FreeVersionCheck;
import com.danh3945.simplespeeddial.widget.LargeWidgetProvider;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class LargeWidgetObject extends WidgetObject {

    @PrimaryKey(autoGenerate = true)
    private long databaseId;
    private String contactId;
    private String name;
    private String number;
    private String numberType;
    private Uri lookupUri;

    public LargeWidgetObject() {
    }

    public static LargeWidgetObject createObject(String name, String number, String numberType) {
        LargeWidgetObject largeWidgetObject = new LargeWidgetObject();
        largeWidgetObject.setName(name);
        largeWidgetObject.setNumber(number);
        largeWidgetObject.setNumberType(numberType);
        return largeWidgetObject;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    @Override
    public Uri getLookupUri() {
        if (lookupUri == null) {
            lookupUri = new Uri.Builder().build();
        }
        return lookupUri;
    }

    public void setLookupUri(Uri lookupUri) {
        this.lookupUri = lookupUri;
    }

    public void addToLargeWidgetSpeedDial(Context context) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpeedDialDatabase database = SpeedDialDatabase.getSpeedDialDatabase(context);

                if (!FreeVersionCheck.canAddLargeWidgetItem(context)) {

                    notifyUserFreeVersionFull(context);
                    return;
                }

                database.largeWidgetDao()
                        .insertSpeedDialButton(LargeWidgetObject.this);

                notifyUserAddedToLargeWidget(context);

                LargeWidgetProvider.notifyLargeWidgets(context);

                updateWidgetCount(context,1);
            }
        });
    }

    private void updateWidgetCount(Context context, int change) {
        // We keep a running tally of the total number of items being displayed by the large widget.
        // This is done so the free version can track how many are displayed.
        // We store them in shared prefs so we don't have to access the database elsewhere and throw
        // around a bunch of extra thread switches that come with DB access.

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Total current widgets
        int count = prefs.getInt(FreeVersionCheck.LARGE_WIDGET_TOTAL_ITEMS_KEY, 0);

        // Modify the current count by the incoming change
        count += change;

        // Store the new value
        prefs.edit().putInt(FreeVersionCheck.LARGE_WIDGET_TOTAL_ITEMS_KEY, count).apply();

    }

    private void notifyUserAddedToLargeWidget(Context context) {
        // We want to show a toast telling the user that we have added the information to the speed
        // dial widget but we have to do toast on the main thread.  So we create a handler linked to
        // the main looper and post a runnable so it gets run on the UI thread.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Added to large widget speed dial list", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void notifyUserFreeVersionFull(Context context) {
        // Make sure we're on the UI thread since there's lots of threading going on in this class.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FreeVersionCheck.getFreeVersionRefusalDialog(context, null).show();
            }
        });
    }

    public void removeFromSpeedDial(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpeedDialDatabase
                        .getSpeedDialDatabase(context)
                        .largeWidgetDao()
                        .removeSpeedDialEntry(LargeWidgetObject.this);

                LargeWidgetProvider.notifyLargeWidgets(context);

                updateWidgetCount(context, -1);
            }
        });
    }
}
