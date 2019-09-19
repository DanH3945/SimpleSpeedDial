package com.danh3945.simplespeeddial.views.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.danh3945.simplespeeddial.R;

public class UseContactPhoto {

    public static boolean shouldUseContactPhoto(Context applicationContext) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String key = applicationContext.getResources().getString(R.string.shared_pref_use_contact_photo_key);
        return prefs.getBoolean(key, true);
    }
}
