package com.danh3945.simplespeeddial.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.danh3945.simplespeeddial.R;

import java.util.List;

import javax.annotation.Nullable;

public class BillingManager implements PurchasesUpdatedListener {

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @androidx.annotation.Nullable List<Purchase> purchases) {

    }

    public enum Result {PREMIUM, NOT_PREMIUM, NET_ERROR}

    public interface PremiumConfirmation {
        void isPremium(Boolean isPremium, Result result);
    }

    private interface ConnectionReady {
        void connectionReady();
    }

    private Activity mHostActivity;
    private BillingClient mBillingClient;

    public BillingManager(Activity activity) {
        mHostActivity = activity;
    }

    private void startConnection(@Nullable ConnectionReady connectionReady) {
        mBillingClient = BillingClient.newBuilder(mHostActivity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (connectionReady != null) {
                    connectionReady.connectionReady();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    public void isPremiumClient(PremiumConfirmation premiumConfirmation) {

        // If the billing client is already running we just use that and move forward.
        if (mBillingClient != null && mBillingClient.isReady()) {
            checkPremium(premiumConfirmation);
        }

        // The billing client wasn't connected so we start the connection then move forward.
        startConnection(() -> checkPremium(premiumConfirmation));
    }

    private void checkPremium(PremiumConfirmation premiumConfirmation) {
        premiumConfirmation.isPremium(true, Result.PREMIUM);
        //premiumConfirmation.isPremium(false, Result.NOT_PREMIUM);
    }

    public AlertDialog getFreeVersionRefusalDialog(Context context, @Nullable DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle(R.string.free_version_alert_title);
        alertBuilder.setMessage(R.string.free_version_too_many_single_tile_widgets);

        alertBuilder.setCancelable(false);

        if (onClickListener == null) {
            onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing here.  The default
                }
            };
        }

        alertBuilder.setNeutralButton(R.string.free_version_button_text, onClickListener);

        return alertBuilder.create();

    }

}
