package com.danh3945.simplespeeddial.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.widget.LargeWidgetProvider;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class LargeWidgetObject {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
                SpeedDialDatabase
                        .getSpeedDialDatabase(context)
                        .largeWidgetDao()
                        .insertSpeedDialButton(LargeWidgetObject.this);

                notifyUserAddedToLargeWidget(context);

                LargeWidgetProvider.notifyLargeWidgets(context);
            }
        });
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

    public Bitmap getContactPhoto(Context context) {
        Bitmap bitmap = ImageHelper.getContactPhoto(context, getLookupUri());
        if (bitmap == null) {
            return getDefaultIcon(context);
        }
        return bitmap;
    }

    public Bitmap getContactPhotoRounded(Context context) {
        Bitmap bitmap = ImageHelper.getContactPhotoRounded(context, getLookupUri());
        if (bitmap == null) {
            return getDefaultIcon(context);
        }
        return bitmap;
    }

    private Bitmap getDefaultIcon(Context context) {
        int defaultColor = ImageHelper.getRandomContactIconColorInt(context, getName());
        return ImageHelper.getDefaultContactIcon(context, defaultColor);
    }
}
