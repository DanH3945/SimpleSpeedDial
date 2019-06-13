package com.danh3945.simplespeeddial.database;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

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
