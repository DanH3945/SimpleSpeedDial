package com.danh3945.simplespeeddial.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

public class WidgetColorController {

    public static final String SINGLE_TILE_TEXT_COLOR_KEY = "singleTileTextColorKey";

    public static final String LARGE_WIDGET_TEXT_COLOR_KEY = "largeWidgetTextColorKey";

    private static void setWidgetTextColor(Context context, String key, int color) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, color).apply();
        SingleTileAppWidgetProvider.notifySingleTileWidgets(context);
        LargeWidgetProvider.notifyLargeWidgets(context);
    }

    private static int getWidgetTextColor(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, Color.WHITE);
    }

    // Large Widget Color prefs aren't fully implemented yet.
    // Todo after launch implement large widget background and text color changing
    public static int getLargeWidgetTextColor(Context context) {
        return getWidgetTextColor(context, LARGE_WIDGET_TEXT_COLOR_KEY);
    }

    public static void setLargeWidgetTextcolor(Context context, int color) {
        setWidgetTextColor(context, LARGE_WIDGET_TEXT_COLOR_KEY, color);
    }

    // Single Tile Widget color prefs aren't fully implemented yet.
    // Todo after launch implement single tile widget text color changing
    public static int getSingleTileWidgetTextColor(Context context) {
        return getWidgetTextColor(context, SINGLE_TILE_TEXT_COLOR_KEY);
    }

    public static void setSingleTileWidgetTextColor(Context context, int color) {
        setWidgetTextColor(context, SINGLE_TILE_TEXT_COLOR_KEY, color);
    }
}
