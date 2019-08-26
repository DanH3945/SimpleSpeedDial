package com.danh3945.simplespeeddial.database;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
@TypeConverters(LocalTypeConverters.class)
public class SingleTileWidgetObject extends WidgetObject {

    @PrimaryKey(autoGenerate = false)
    int widgetId;
    String name;
    String number;
    Uri lookupUri;

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
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

    @Override
    public Uri getLookupUri() {
        return lookupUri;
    }

    public void setLookupUri(Uri lookupUri) {
        this.lookupUri = lookupUri;
    }

    public static SingleTileWidgetObject fromLargeWidgetObject(int appWidgetId, LargeWidgetObject largeWidgetObject) {
        SingleTileWidgetObject singleTileWidgetObject = new SingleTileWidgetObject();

        singleTileWidgetObject.setWidgetId(appWidgetId);
        singleTileWidgetObject.setName(largeWidgetObject.getName());
        singleTileWidgetObject.setNumber(largeWidgetObject.getNumber());
        singleTileWidgetObject.setLookupUri(largeWidgetObject.getLookupUri());

        return singleTileWidgetObject;
    }

    public void addToDatabase(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpeedDialDatabase.getSpeedDialDatabase(context).singleTileWidgetDao().insertSingleTileData(SingleTileWidgetObject.this);
            }
        });
    }

    public void removeFromDatabase(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpeedDialDatabase.getSpeedDialDatabase(context).singleTileWidgetDao().deleteSingleTileData(SingleTileWidgetObject.this);
            }
        });
    }
}
