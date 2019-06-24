package com.danh3945.simplespeeddial.views;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.danh3945.simplespeeddial.BuildConfig;
import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.logging.TimberDebugTree;
import com.danh3945.simplespeeddial.logging.TimberReleaseTree;
import com.danh3945.simplespeeddial.views.primaryDisplay.PrimaryDisplayFragment;
import com.google.android.gms.ads.MobileAds;

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

        String adMobID = getResources().getString(R.string.ad_mob_app_id);
        MobileAds.initialize(this, adMobID);

        if (savedInstanceState == null) {
            loadFragment(PrimaryDisplayFragment.createInstance(), true, PrimaryDisplayFragment.TAG);
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.overflow_menu_about:
                new AboutDialogFragment().show(getSupportFragmentManager(), null);
                break;

            case R.id.overflow_menu_preferences:
                // No preferences yet
                // Todo implement me
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
