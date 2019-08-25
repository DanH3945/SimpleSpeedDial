package com.danh3945.simplespeeddial.views.welcomeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.views.primaryDisplay.PrimaryDisplayFragment;

public class WelcomeFragment extends Fragment {

    public static final String TAG = "WelcomeFragment";

    public static Fragment createInstance() {
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_screen, container, false);

        Button getStartedButton = view.findViewById(R.id.welcome_fragment_get_started_button);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.main_fragment_container, PrimaryDisplayFragment.createInstance(), PrimaryDisplayFragment.TAG);
                ft.addToBackStack(PrimaryDisplayFragment.TAG);
                ft.commit();
                fm.executePendingTransactions();
            }
        });

        return view;
    }
}
