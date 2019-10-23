package com.danh3945.simplespeeddial.database;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.danh3945.simplespeeddial.billing.BillingManager;
import com.danh3945.simplespeeddial.billing.FreeWidgetConstants;
import com.danh3945.simplespeeddial.widget.LargeWidgetProvider;

import timber.log.Timber;

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

        // Working with the database here so we'll wrap this all in an async task.

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // calling the actual setup code for the large widget.  It's separated here
                // to aid readability.
                setupLargeWidget(context);
            }
        });
    }

    private void setupLargeWidget(Context context) {
        // This method should really only be called from the method directly above since it needs
        // to run Async to work with the database.
        Activity callingActivity;
        try {
            callingActivity = (Activity) context;
        } catch (ClassCastException cce) {
            Timber.d("Failed to cast context into Activity");
            return;
        }

        BillingManager billingManager = BillingManager.getBillingManager(callingActivity);
        billingManager.checkPremium(new BillingManager.PremiumConfirmation() {
            @Override
            public void isPremium(Boolean isPremium, BillingManager.Result resultCode) {

                switch (resultCode) {

                    case PREMIUM:
                        addToDatabase(context);
                        break;

                    case NOT_PREMIUM:
                        if (canAddFreeWidget(context)) {
                            addToDatabase(context);
                            break;
                        }

                        // If the free version attempt above fails we notify the user
                        notifyUserFreeVersionFull(context, billingManager);
                        break;

                    case NET_ERROR:
                        if(canAddFreeWidget(context)) {
                            addToDatabase(context);
                            break;
                        }

                        // todo error notification and handling if we can't use the free version


                }
            }
        });
    }

    private boolean canAddFreeWidget(Context context) {
        int widgetCount = SpeedDialDatabase
                .getSpeedDialDatabase(context)
                .largeWidgetDao()
                .getSpeedDialButtonsList()
                .size();

        return widgetCount < FreeWidgetConstants.MAX_LARGE_WIDGET_BUTTONS;
    }

    private void addToDatabase(Context context) {
        SpeedDialDatabase database = SpeedDialDatabase.getSpeedDialDatabase(context);
        database.largeWidgetDao()
                .insertSpeedDialButton(LargeWidgetObject.this);

        notifyUserWidgetWasAdded(context);

        LargeWidgetProvider.notifyLargeWidgets(context);

    }

    private void notifyUserWidgetWasAdded(Context context) {
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

    private void notifyUserFreeVersionFull(Context context, BillingManager billingManager) {
        // Make sure we're on the UI thread since there's lots of threading going on in this class.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                billingManager.getFreeVersionRefusalDialog(context, null).show();
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
            }
        });
    }
}
