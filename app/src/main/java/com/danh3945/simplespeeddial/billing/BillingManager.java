package com.danh3945.simplespeeddial.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.danh3945.simplespeeddial.R;

import java.util.List;

import javax.annotation.Nullable;

public class BillingManager implements LifecycleEventObserver, PurchasesUpdatedListener {

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @androidx.annotation.Nullable List<Purchase> purchases) {

    }

    public enum Result {PREMIUM, NOT_PREMIUM, NET_ERROR}

    public interface PremiumConfirmation {
        void isPremium(Result result);
    }

    private interface ConnectionReady {
        void connectionReady();
    }

    private AppCompatActivity mHostActivity;
    private BillingClient mBillingClient;
    private Purchase.PurchasesResult purchasesResult;

    private static final String SUBSCRIPTION_SKU = "SimpleSpeedDialSub";

    public BillingManager(AppCompatActivity activity) {
        mHostActivity = activity;
        mHostActivity.getLifecycle().addObserver(this);

        queryPurchases();

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
        for (Purchase purchase : purchasesResult.getPurchasesList()) {
            if (purchase.getSku().equals(SUBSCRIPTION_SKU) &&
                    purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                premiumConfirmation.isPremium(Result.PREMIUM);
                return;
            }

            premiumConfirmation.isPremium(Result.NOT_PREMIUM);
        }
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

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event.equals(Lifecycle.Event.ON_RESUME)) {
            queryPurchases();
        }
    }

    private void queryPurchases() {
        purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
    }

}
