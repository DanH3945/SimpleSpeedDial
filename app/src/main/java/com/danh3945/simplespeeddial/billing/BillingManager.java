package com.danh3945.simplespeeddial.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.danh3945.simplespeeddial.R;

import java.util.List;

import javax.annotation.Nullable;

import timber.log.Timber;

public class BillingManager implements PurchasesUpdatedListener {

    public enum Result {PREMIUM, NOT_PREMIUM, NET_ERROR}

    private interface ConnectionReady {
        void connectionReady(int billingResponseCode);
    }

    private BillingClient mBillingClient;

    private static final String SUBSCRIPTION_SKU = "SimpleSpeedDialSub";

    public BillingManager(AppCompatActivity activity) {

        mBillingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();

    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @androidx.annotation.Nullable List<Purchase> purchases) {

    }

    private void startConnection(ConnectionReady connectionReady) {

        if (mBillingClient != null && mBillingClient.isReady()) {

            if (connectionReady != null) {
                connectionReady.connectionReady(BillingClient.BillingResponseCode.OK);
            }

            return;
        }

        Timber.d("Starting BillingClient connection");
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Timber.d("Billing setup finished.");
                connectionReady.connectionReady(billingResult.getResponseCode());
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    public interface PremiumConfirmation {
        void isPremium(Result result);
    }

    public void isPremiumClient(PremiumConfirmation premiumConfirmation) {

        startConnection(new ConnectionReady() {
            @Override
            public void connectionReady(int billingResponseCode) {

                if (billingResponseCode != BillingClient.BillingResponseCode.OK) {
                    premiumConfirmation.isPremium(Result.NET_ERROR);
                    return;
                }

                Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);

                if (purchasesResult == null) {
                    premiumConfirmation.isPremium(Result.NOT_PREMIUM);
                    return;
                }

                for (Purchase purchase : purchasesResult.getPurchasesList()) {
                    Timber.d(purchase.getSku());
                    if (purchase.getSku().equals(SUBSCRIPTION_SKU) &&
                            purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        premiumConfirmation.isPremium(Result.PREMIUM);
                        return;
                    }
                }

                premiumConfirmation.isPremium(Result.NOT_PREMIUM);
            }
        });

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
