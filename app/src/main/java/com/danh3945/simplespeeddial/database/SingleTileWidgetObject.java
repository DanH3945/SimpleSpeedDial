package com.danh3945.simplespeeddial.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class SingleTileWidgetObject {

    @PrimaryKey(autoGenerate = false)
    int widgetId;
    String name;
    String number;
    String lookupUri;
    int defaultColor;

    public interface SingleTileListCallback {
        void callback(List<SingleTileWidgetObject> list);
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
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

    public String getLookupUri() {
        return lookupUri;
    }

    public void setLookupUri(String lookupUri) {
        this.lookupUri = lookupUri;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public static SingleTileWidgetObject fromLargeWidgetObject(int appWidgetId, int defaultColor, LargeWidgetObject largeWidgetObject) {
        SingleTileWidgetObject singleTileWidgetObject = new SingleTileWidgetObject();

        singleTileWidgetObject.setWidgetId(appWidgetId);
        singleTileWidgetObject.setName(largeWidgetObject.getName());
        singleTileWidgetObject.setNumber(largeWidgetObject.getNumber());
        singleTileWidgetObject.setLookupUri(largeWidgetObject.getLookupUri().toString());
        singleTileWidgetObject.setDefaultColor(defaultColor);

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

    public static void getSingleTileWidgetList(Context context, SingleTileListCallback singleTileListCallback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<SingleTileWidgetObject> objects = SpeedDialDatabase.getSpeedDialDatabase(context).singleTileWidgetDao().getSingleTileDataList();
                singleTileListCallback.callback(objects);
            }
        });
    }
}
