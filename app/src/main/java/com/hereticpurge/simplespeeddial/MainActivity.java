package com.hereticpurge.simplespeeddial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hereticpurge.simplespeeddial.logging.TimberDebugTree;
import com.hereticpurge.simplespeeddial.logging.TimberReleaseTree;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Timber logging load
        if (BuildConfig.DEBUG) {
            Timber.plant(new TimberDebugTree());
        } else {
            Timber.plant(new TimberReleaseTree());
        }
        // Nothing new above this line

        // Request Permissions above SDK 23
        if (BuildConfig.VERSION_CODE < 23) {

        }

        if (savedInstanceState == null) {
            //loadFragment(PrimaryDisplayFragment.createInstance(), true, PrimaryDisplayFragment.TAG);
            loadFragment(ContactListFragment.createInstance(), true, ContactListFragment.TAG);
        }
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);

        if (addToBackStack) fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }
}
