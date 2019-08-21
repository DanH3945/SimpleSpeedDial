package com.danh3945.simplespeeddial.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.danh3945.simplespeeddial.BuildConfig;
import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.logging.TimberDebugTree;
import com.danh3945.simplespeeddial.logging.TimberReleaseTree;
import com.danh3945.simplespeeddial.views.preferences.SpeedDialPreferenceFragment;
import com.danh3945.simplespeeddial.widget.LargeWidgetProvider;
import com.danh3945.simplespeeddial.widget.SingleTileAppWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import timber.log.Timber;

public abstract class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Timber logging load
        if (BuildConfig.DEBUG) {
            Timber.plant(new TimberDebugTree());
        } else {
            Timber.plant(new TimberReleaseTree());
        }
        // Nothing new above this line

        // Mobile Ads initialization
        String adMobID = getResources().getString(R.string.ad_mob_app_id);
        MobileAds.initialize(this, adMobID);
        if (BuildConfig.FLAVOR.equals("free")) {
            initMobileAds();
        }

        if (isLandscapeOriented()) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                !isLandscapeOriented()) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void initMobileAds() {

        if (isLandscapeOriented()) {
            FrameLayout adContainer = findViewById(R.id.banner_ad_container);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) adContainer.getLayoutParams();
            params.horizontalBias = 0.97f;
            adContainer.setLayoutParams(params);
        }

        AdView bannerAd = findViewById(R.id.banner_ad_view);

        AdRequest.Builder bannerAdRequestBuilder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            Timber.d("Debug build detected while initializing mobile ads.  Adding Test Device.");
            bannerAdRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        }
        bannerAd.loadAd(bannerAdRequestBuilder.build());
    }

    private boolean isLandscapeOriented() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    void loadFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);

        if (addToBackStack) fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
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
                loadFragment(SpeedDialPreferenceFragment.createInstance(getApplicationContext()), true, SpeedDialPreferenceFragment.TAG);
                break;

            case R.id.overflow_menu_refresh:
                SingleTileAppWidgetProvider.notifySingleTileWidgets(this);
                LargeWidgetProvider.notifyLargeWidgets(this);
                Toast.makeText(this, R.string.menu_refreshed_text, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}