package com.hereticpurge.simplespeeddial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public class PrimaryDisplayFragment extends Fragment {

    public static final String TAG = "SpeedDialPrimaryDisplayFragment";

    public static Fragment createInstance() {
        return new PrimaryDisplayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Creating View for %s", TAG);
        View view = inflater.inflate(R.layout.primary_display_layout, container, false);

        return view;
    }
}
