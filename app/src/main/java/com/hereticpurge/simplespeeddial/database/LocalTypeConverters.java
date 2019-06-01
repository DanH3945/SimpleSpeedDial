package com.hereticpurge.simplespeeddial.database;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class LocalTypeConverters {

    @TypeConverter
    public static String getStringFromUri(Uri uri) {
        return uri.toString();
    }

    @TypeConverter
    public static Uri getUriFromString(String string) {
        return Uri.parse(string);
    }
}
