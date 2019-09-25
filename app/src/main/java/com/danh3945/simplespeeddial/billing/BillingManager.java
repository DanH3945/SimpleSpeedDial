package com.danh3945.simplespeeddial.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.widget.SingleTileAppWidgetProvider;

import javax.annotation.Nullable;

public class BillingManager {

    private static BillingManager sBillingManager;

    public static final String LARGE_WIDGET_TOTAL_ITEMS_KEY = "totalLargeWidgetItems";

    // Maximum number of free widgets
    public static final int MAX_FREE_SINGLE_TILE_SLOTS = 5;
    public static final int MAX_FREE_LARGE_SLOTS = 5;

    public static BillingManager getBillingManager(Context context) {
        if (sBillingManager == null) {
            sBillingManager = new BillingManager();
        }

        return sBillingManager;
    }

    public boolean isPremium() {
        return false;
    }


    public boolean canAddSingleTileWidget(Context context) {

        if (isPremium()) {
            return true;
        }

        int[] widgetIds = SingleTileAppWidgetProvider.getActiveWidgetIds(context);

        return widgetIds.length <= MAX_FREE_SINGLE_TILE_SLOTS;
    }

    public boolean canAddLargeWidgetItem(Context context) {

        if (isPremium()) {
            return true;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int largeItemsCount = prefs.getInt(LARGE_WIDGET_TOTAL_ITEMS_KEY, 0);

        return largeItemsCount < MAX_FREE_LARGE_SLOTS;
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
