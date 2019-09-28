package com.danh3945.simplespeeddial.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.danh3945.simplespeeddial.R;

import javax.annotation.Nullable;

public class BillingManager {

    public interface PremiumConfirmation {
        void isPremium(Boolean isPremium);
    }

    private static BillingManager sBillingManager;

    public static BillingManager getBillingManager(Context context) {
        if (sBillingManager == null) {
            sBillingManager = new BillingManager();
        }

        return sBillingManager;
    }

    public void checkPremium(PremiumConfirmation premiumConfirmation) {
        premiumConfirmation.isPremium(false);
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