package com.danh3945.simplespeeddial.views;

import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.billing.BillingManager;
import com.danh3945.simplespeeddial.views.singleTileconfig.SingleTileConfigFragment;
import com.danh3945.simplespeeddial.widget.SingleTileAppWidgetProvider;

import timber.log.Timber;

public class SingleTileConfigActivity extends ParentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Timber.d("Starting Single Tile Configuration");

        // Unusually we're calling setContentView before the super call to onCreate.  This is so
        // the mobile ads in the parent class has something to bind to.  The parent class then calls
        // the actual AppCompatActivity super class.
        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        BillingManager billingManager = BillingManager.getBillingManager(this);
        int[] singleTileIds = SingleTileAppWidgetProvider.getActiveWidgetIds(this);
        Timber.d("Total single tile Widget IDs is: %s", singleTileIds.length);

        billingManager.checkPremium(new BillingManager.PremiumConfirmation() {
            @Override
            public void isPremium(Boolean isPremium, BillingManager.Result resultCode) {
                if (isPremium) {
                    continueSetup();
                } else if (resultCode == BillingManager.Result.NOT_PREMIUM) {
                    billingManager.getFreeVersionRefusalDialog(
                            SingleTileConfigActivity.this, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SingleTileConfigActivity.this.finish();
                                }
                            }).show();
                } else if (resultCode == BillingManager.Result.NET_ERROR) {
                    // todo error network error handling
                }
            }
        });
    }

    private void continueSetup() {

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

        loadFragment(SingleTileConfigFragment.createInstance(finalAppWidgetId), false, SingleTileConfigFragment.TAG);

    }
}
