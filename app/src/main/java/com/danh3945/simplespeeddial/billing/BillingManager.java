package com.danh3945.simplespeeddial.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

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

import timber.log.Timber;

public class BillingManager implements LifecycleEventObserver, PurchasesUpdatedListener {

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @androidx.annotation.Nullable List<Purchase> purchases) {
        queryPurchases();
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
        Timber.d("Starting BillingClient connection");
        mBillingClient = BillingClient.newBuilder(mHostActivity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Timber.d("Billing setup finished.");
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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                startPremiumClientWaitingThread(premiumConfirmation);
            }
        });
    }

    private void startPremiumClientWaitingThread(PremiumConfirmation premiumConfirmation) {

        // Sometimes on startup of the app the client will not have connected to verify the purchase.
        // So we do a little sleep on the confirmation thread to ensure that billingClient hsa time
        // to do its connection.
        int sleepCounter = 0;
        while (purchasesResult == null) {
            try {
                sleepCounter++;
                Thread.sleep(1000);
                if (sleepCounter > 10) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Returning to the main thread with the result.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (purchasesResult == null) {
                    premiumConfirmation.isPremium(Result.NET_ERROR);
                    return;
                }

                Timber.d("Iterating over PurchasesResult.  Total length:  %s", purchasesResult.getPurchasesList().size());
                for (Purchase purchase : purchasesResult.getPurchasesList()) {
                    Timber.d(purchase.getSku());
                    if (purchase.getSku().equals(SUBSCRIPTION_SKU) &&
                            purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        premiumConfirmation.isPremium(Result.PREMIUM);
                        return;
                    }
                }

                // The final default if nothing else returns the method.
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

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event.equals(Lifecycle.Event.ON_RESUME)) {
            queryPurchases();
        }
    }

    private void queryPurchases() {

        if (mBillingClient == null | purchasesResult == null) {

            // TODO see if queryPurchases needs the actual network connection and adjust.
            Timber.d("Billing client was not connected.  Calling startConnection.");
            startConnection(new ConnectionReady() {
                @Override
                public void connectionReady() {
                    Timber.d("Connection Ready in queryPurchases. Querying.");
                    purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                }
            });

            return;
        }

        purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
    }

}
