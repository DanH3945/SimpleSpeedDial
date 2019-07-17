package com.danh3945.simplespeeddial.views.preferences;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.widget.WidgetProvider;

import timber.log.Timber;

public class SpeedDialPreferenceFragment extends PreferenceFragmentCompat {
    public static final String TAG = "SpeedDialPreferenceFragment";

    private static final int PERMISSION_REQUEST_INSTANT_CALL = 5200;

    private Context mContext;

    public static Fragment createInstance(Context context) {
        return new SpeedDialPreferenceFragment(context);
    }

    SpeedDialPreferenceFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
        setRequestPermissions();
    }

    private void setRequestPermissions() {
        CheckBoxPreference instantCallPref =
                findPreference(getResources().getString(R.string.shared_pref_dial_type_key));

        instantCallPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Timber.d("Attempting to change instant call pref to %s", (boolean) newValue);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED && !shouldInstantDial()) {
                    // We do not have permission so ask for it. Get the result in the
                    // onRequestPermissionResult method.
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_INSTANT_CALL);
                    return false;
                }
                WidgetProvider.notifyWidgets(mContext);
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_INSTANT_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    String instantDialKey = getResources().getString(R.string.shared_pref_dial_type_key);
                    CheckBoxPreference instantCallPref = findPreference(instantDialKey);
                    try {
                        instantCallPref.setChecked(true);
                    } catch (NullPointerException npe) {
                        Timber.i(npe);
                    }
                    WidgetProvider.notifyWidgets(mContext);
                }
        }
    }

    private boolean shouldInstantDial() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        String instantDialKey = getResources().getString(R.string.shared_pref_dial_type_key);

        return prefs.getBoolean(instantDialKey, false);
    }

}
