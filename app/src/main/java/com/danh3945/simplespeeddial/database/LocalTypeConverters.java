package com.danh3945.simplespeeddial.database;

import android.net.Uri;

import androidx.room.TypeConverter;

public class LocalTypeConverters {

    @TypeConverter
    public static String getStringFromUri(Uri uri) {
        return uri != null ? uri.toString() : null;
    }

    @TypeConverter
    public static Uri getUriFromString(String string) {
        return string != null ? Uri.parse(string) : null;
    }
}
