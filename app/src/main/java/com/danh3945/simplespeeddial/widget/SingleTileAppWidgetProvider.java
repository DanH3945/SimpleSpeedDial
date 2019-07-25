package com.danh3945.simplespeeddial.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.views.preferences.InstantDial;

import timber.log.Timber;

public class SingleTileAppWidgetProvider extends AppWidgetProvider {

    private static final String NUMBER_KEY = "numberKey";
    private static final String LOOKUP_URI_KEY = "lookupURIKey";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get and update each of the single tile widgets associated with this provider.

        Timber.d("Single Tile Widget - Called onUpdate");
        for (int appWidgetId : appWidgetIds) {
            Timber.d("Updating app widget with ID: %s", appWidgetId);

            // Define the view for each widget.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_layout);

            // Unpack the AppWidgetOptions Bundle.
            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

            // Get the phone number associated with the widget.
            String number = options.getString(NUMBER_KEY);

            // Update the pending intent for the number to reflect preferences set by the user.
            if (number != null) {
                Timber.d("Single Tile Widget - number was NOT NULL.  Setting pending intent.");
                PendingIntent pendingIntent = getDialPendingIntent(context, number);

                views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);
            } else {
                Timber.d("Single Tile Widget - number was NULL");
            }

            // Get the lookupUri and use it to retrieve the current user thumbnail and apply
            // it to the widget.
            Uri lookupUri = Uri.parse(options.getString(LOOKUP_URI_KEY));
            Bitmap bitmap = ImageHelper.getContactPhotoRounded(context, lookupUri);
            views.setImageViewBitmap(R.id.widget_single_image, bitmap);

            // Our changes to the widgets are done so we send off the updates to the widget manager
            // so it can complete its updates.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    public static void notifySingleTileWidgets(Context context) {

        // Grab all the current Widgets associated with this Provider and tell them to update their
        // state.  Eventually calls the OnUpdate function above.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName componentName = new ComponentName(context, SingleTileAppWidgetProvider.class.getName());

        int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);

        Intent intent = new Intent(context, SingleTileAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        context.sendBroadcast(intent);
    }

    public static void setupSingleTileWidget(Context context, int appWidgetId, SpeedDialObject speedDialObject) {

        // Called by any configuration activity to setup a single tile widget with the parameters
        // in the SpeedDialObject.

        Timber.d("Setting up widget with ID: %s", appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_single_layout);

        // Set the display text.
        views.setTextViewText(R.id.widget_single_text, speedDialObject.getName());

        // Set the image.
        Bitmap bitmap = ImageHelper.getContactPhotoRounded(context, speedDialObject.getLookup_uri());
        views.setImageViewBitmap(R.id.widget_single_image, bitmap);

        // Set the onClick PendingIntent to be run when the user clicks the widget.
        PendingIntent pendingIntent = getDialPendingIntent(context, speedDialObject.getNumber());
        views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);

        // Store the phone number and lookup URI to be used in future calls to onUpdate so we can
        // make sure the loaded thumbnail is correct.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options != Bundle.EMPTY) {
            options.putString(LOOKUP_URI_KEY, speedDialObject.getLookup_uri().toString());
            options.putString(NUMBER_KEY, speedDialObject.getNumber());
            appWidgetManager.updateAppWidgetOptions(appWidgetId, options);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static PendingIntent getDialPendingIntent(Context context, String number) {

        Intent intent;

        // Check to see if the user wants to use instant dial.
        if (InstantDial.shouldInstantDial(context)) {
            intent = new Intent(Intent.ACTION_CALL);
        } else {
            intent = new Intent(Intent.ACTION_DIAL);
        }

        // Setup the URI for the call and attach it to the intent.
        String callUri = "tel:" + number;
        intent.setData(Uri.parse(callUri));

        // Package the Intent in a PendingIntent and return it.
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}
