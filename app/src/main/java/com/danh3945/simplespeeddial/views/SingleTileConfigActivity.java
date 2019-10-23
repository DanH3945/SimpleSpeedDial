package com.danh3945.simplespeeddial.views;

import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.billing.BillingManager;
import com.danh3945.simplespeeddial.billing.FreeWidgetConstants;
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

        billingManager.checkPremium(new BillingManager.PremiumConfirmation() {
            @Override
            public void isPremium(Boolean isPremium, BillingManager.Result resultCode) {

                switch (resultCode) {
                    case PREMIUM:
                        continueSetup();
                        break;

                    case NOT_PREMIUM:
                        if (canAddFreeWidget()) {
                            continueSetup();
                            break;
                        }

                        // If the free version setup doesn't work (The user is over limit on the free version
                        // we show the notification that the user is over the limit.
                        billingManager.getFreeVersionRefusalDialog(
                                SingleTileConfigActivity.this, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SingleTileConfigActivity.this.finish();
                                    }
                                }).show();
                        break;

                    case NET_ERROR:
                        if (canAddFreeWidget()) {
                            continueSetup();
                            break;
                        }
                        // todo network error notification
                }
            }
        });
    }

    private boolean canAddFreeWidget() {
        int[] singleTileIds = SingleTileAppWidgetProvider.getActiveWidgetIds(this);

        return singleTileIds.length < FreeWidgetConstants.MAX_SINGLE_TILE_FREE_WIDGETS;
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
