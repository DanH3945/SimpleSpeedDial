package com.danh3945.simplespeeddial.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.danh3945.simplespeeddial.widget.WidgetProvider;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class QuickContact {

    @PrimaryKey(autoGenerate = true)
    private long databaseId;
    private String contactId;
    private String name;
    private String number;
    private String numberType;
    private Uri lookup_uri;

    public QuickContact() {
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
                QuickContactDatabase
                        .getQuickContactDatabase(context)
                        .quickContactDao()
                        .insertContact(QuickContact.this);

                Toast.makeText(context, "Added to speed dial list", Toast.LENGTH_LONG).show();

                WidgetProvider.notifyWidgets(context);
            }
        });
    }

    public void removeFromSpeedDial(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                QuickContactDatabase
                        .getQuickContactDatabase(context)
                        .quickContactDao()
                        .removeSpeedDialEntry(QuickContact.this);

                WidgetProvider.notifyWidgets(context);
            }
        });
    }
}
