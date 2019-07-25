package com.danh3945.simplespeeddial.views.preferences;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;

import com.danh3945.simplespeeddial.R;

public class InstantDial {

    public static boolean shouldInstantDial(Context applicationContext) {
        // First check for the permission to instant dial.  If not just return false since
        // the app can't instant dial without it.
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        // We have permission to instant dial so we check to see if the user has checked the pref
        // to instant dial and return the result.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String key = applicationContext.getResources().getString(R.string.shared_pref_dial_type_key);
        return prefs.getBoolean(key, false);
    }
}
