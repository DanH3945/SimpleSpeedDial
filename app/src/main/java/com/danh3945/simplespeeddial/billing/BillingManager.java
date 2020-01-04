package com.danh3945.simplespeeddial.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import timber.log.Timber;

public class BillingManager implements LifecycleEventObserver, PurchasesUpdatedListener {

    public enum Result {PREMIUM, NOT_PREMIUM, NET_ERROR}

    public interface BillingListener {
        void purchasesUpdated(Result result);
    }

    private interface ConnectionReady {
        void connectionReady();
    }

    private List<BillingListener> mListeners;
    private BillingClient mBillingClient;
    private Purchase.PurchasesResult mPurchasesResult;

    private static final String SUBSCRIPTION_SKU = "SimpleSpeedDialSub";

    public BillingManager(AppCompatActivity activity) {

        mBillingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        activity.getLifecycle().addObserver(this);
        mListeners = new ArrayList<>();

    }

    public void observeWithResult(BillingListener billingListener) {
        observe(billingListener);
        isPremiumClient(new PremiumConfirmation() {
            @Override
            public void isPremium(Result result) {
                billingListener.purchasesUpdated(result);
            }
        });
    }

    public void observe(BillingListener billingListener) {
        mListeners.add(billingListener);
    }

    private void notifyObservers() {
        if (mListeners.size() < 1) {
            return;
        }

        isPremiumClient(new PremiumConfirmation() {
            @Override
            public void isPremium(Result result) {
                for (BillingListener billingListener : mListeners) {
                    billingListener.purchasesUpdated(result);
                }
            }
        });
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event.equals(Lifecycle.Event.ON_RESUME)) {
            queryPurchases(null);
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @androidx.annotation.Nullable List<Purchase> purchases) {
        queryPurchases(null);
    }

    private void startConnection(@Nullable ConnectionReady connectionReady) {

        if (mBillingClient != null && mBillingClient.isReady()) {

            if (connectionReady != null) {
                connectionReady.connectionReady();
            }

            return;
        }

        Timber.d("Starting BillingClient connection");
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

    private void queryPurchases(ConnectionReady connectionReady) {

        startConnection(new ConnectionReady() {
            @Override
            public void connectionReady() {
                mPurchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                if (connectionReady != null) {
                    connectionReady.connectionReady();
                }
            }
        });
    }

    public interface PremiumConfirmation {
        void isPremium(Result result);
    }

    public void isPremiumClient(PremiumConfirmation premiumConfirmation) {

        // If the connection isn't ready initially we are going to be starting it.  Since we aren't in control
        // of the threading in that class and it's an internet connection we'll assume that it's using multiple
        // or other threads to do its work.  So we'll save our current thread here and jump back to it later
        // before we post the results to the calling object(s)
        Handler handler = new Handler(Looper.myLooper());


        Runnable premiumCheck = new Runnable() {
            @Override
            public void run() {

                while (mPurchasesResult == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Timber.d("Iterating over PurchasesResult.  Total length:  %s", mPurchasesResult.getPurchasesList().size());

                for (Purchase purchase : mPurchasesResult.getPurchasesList()) {
                    Timber.d(purchase.getSku());
                    if (purchase.getSku().equals(SUBSCRIPTION_SKU) &&
                            purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        premiumConfirmation.isPremium(Result.PREMIUM);
                        return;
                    }
                }

                premiumConfirmation.isPremium(Result.NOT_PREMIUM);
            }
        };

        if (mPurchasesResult == null) {

            startConnection(new ConnectionReady() {
                @Override
                public void connectionReady() {
                    // Once the connection is ready we have to make sure we come back from the connection
                    // thread and post to the incoming thread that we saved at the start of the method.
                    handler.post(premiumCheck);
                }
            });
        } else {
            // we already have a set of purchase results ready so we just iterate over them.
            premiumCheck.run();
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

}
