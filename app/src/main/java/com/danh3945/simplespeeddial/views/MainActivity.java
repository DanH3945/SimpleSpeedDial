package com.danh3945.simplespeeddial.views;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.danh3945.simplespeeddial.BuildConfig;
import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.logging.TimberDebugTree;
import com.danh3945.simplespeeddial.logging.TimberReleaseTree;
import com.danh3945.simplespeeddial.views.contactList.ContactListFragment;
import com.danh3945.simplespeeddial.views.contactList.ContactListRecyclerAdapter;
import com.danh3945.simplespeeddial.views.preferences.SpeedDialPreferenceFragment;
import com.danh3945.simplespeeddial.views.primaryDisplay.PrimaryDisplayFragment;
import com.danh3945.simplespeeddial.widget.SingleTileAppWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

        // Mobile Ads initialization
        String adMobID = getResources().getString(R.string.ad_mob_app_id);
        MobileAds.initialize(this, adMobID);
        if (BuildConfig.FLAVOR.equals("free")) {
            initMobileAds();
        }

        if (isLandscapeOriented()) {
            getSupportActionBar().hide();
        }

        if (getIntent().getAction() != null &&
                getIntent().getAction().equals(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)) {
            // App was started by a single tile widget for configuration.
            loadSingleTileConfigFragment();
        } else {
            // load the main fragment to display to the user.
            loadFragment(PrimaryDisplayFragment.createInstance(), false, PrimaryDisplayFragment.TAG);
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

    private void loadFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);

        if (addToBackStack) fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void loadSingleTileConfigFragment() {

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // Make the id final so it can be used below.
        int finalAppWidgetId = appWidgetId;
        ContactListRecyclerAdapter.ContactListResultCallback callback = new ContactListRecyclerAdapter.ContactListResultCallback() {
            @Override
            public void clickResult(SpeedDialObject object) {
                // Todo configure the single tile widget with the result information.
                Timber.d("Configuring single tile widget with name: %s, number: %s, numberType: %s",
                        object.getName(), object.getNumber(), object.getNumberType());

                SingleTileAppWidgetProvider.setupSingleTileWidget(MainActivity.this, finalAppWidgetId, object);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, finalAppWidgetId);
                MainActivity.this.setResult(RESULT_OK, resultValue);
                finish();
            }
        };

        loadFragment(ContactListFragment.createInstanceForResult(callback), true, ContactListFragment.TAG);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isLandscapeOriented() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
