package com.danh3945.simplespeeddial.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.widget.WidgetProvider;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class SpeedDialBtn {

    @PrimaryKey(autoGenerate = true)
    private long databaseId;
    private String contactId;
    private String name;
    private String number;
    private String numberType;
    private Uri lookup_uri;

    public SpeedDialBtn() {
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

    public Uri getLookup_uri() {
        return lookup_uri;
    }

    public void setLookup_uri(Uri lookup_uri) {
        this.lookup_uri = lookup_uri;
    }

    public void addToSpeedDial(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpeedDialDatabase
                        .getSpeedDialDatabase(context)
                        .speedDialDao()
                        .insertSpeedDialButton(SpeedDialBtn.this);

                notifyUserAddedSpeedDial(context);

                WidgetProvider.notifyWidgets(context);
            }
        });
    }

    private void notifyUserAddedSpeedDial(Context context) {
        // We want to show a toast telling the user that we have added the information to the speed
        // dial widget but we have to do toast on the main thread.  So we create a handler linked to
        // the main looper and post a runnable so it gets run on the UI thread.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Added to speed dial list", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void removeFromSpeedDial(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpeedDialDatabase
                        .getSpeedDialDatabase(context)
                        .speedDialDao()
                        .removeSpeedDialEntry(SpeedDialBtn.this);

                WidgetProvider.notifyWidgets(context);
            }
        });
    }

    public Bitmap getContactPhoto(Context context) {
        return ImageHelper.getContactPhoto(context, getLookup_uri());
    }

    public Bitmap getContactPhotoRounded(Context context) {
        return ImageHelper.getContactPhotoRounded(context, getLookup_uri());
    }
}
