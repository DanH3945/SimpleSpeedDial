package com.danh3945.simplespeeddial.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.FrameLayout;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import timber.log.Timber;

public abstract class MobileAdsActivity extends AppCompatActivity {

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

        AdRequest bannerAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        bannerAd.loadAd(bannerAdRequest);
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
}
