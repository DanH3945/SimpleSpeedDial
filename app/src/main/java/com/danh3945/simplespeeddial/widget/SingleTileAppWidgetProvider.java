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

    private static final String NUMBER_KEY = "speedDialNumberKey";
    private static final String LOOKUP_URI_KEY = "speedDialLookupURIKey";
    private static final String NAME_KEY = "speedDialNameKey";

    private static final String URI_PHONE_SCHEME = "tel";

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
                PendingIntent pendingIntent = getDialPendingIntent(context, number);

                views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);
            } else {
                Timber.d("Number was null for widget %s, skipping", appWidgetId);
            }

            // Get the name of the contact from the widget options.
            String name = options.getString(NAME_KEY);

            // As long as the name isn't null set the name value to the one associated with the widget.
            // If it is null the default value of the TextView in res will remain.
            if (name != null) {
                views.setTextViewText(R.id.widget_single_text, name);
            } else {
                Timber.d("Name was null for widget %s, skipping", appWidgetId);
            }

            // Get the lookupUri and use it to retrieve the current user thumbnail and apply
            // it to the widget.  Otherwise leave the default image specified in the res.
            String lookupUriString = options.getString(LOOKUP_URI_KEY);

            if (lookupUriString != null) {
                Uri lookupUri = Uri.parse(lookupUriString);
                Bitmap bitmap = ImageHelper.getContactPhotoRounded(context, lookupUri);
                views.setImageViewBitmap(R.id.widget_single_image, bitmap);
            } else {
                Timber.d("Null Uri value for widget %s, skipping", appWidgetId);
            }

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

        // Store the basic information for the widget in the widget options itself for use later.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options != Bundle.EMPTY) {
            options.putString(NAME_KEY, speedDialObject.getName());
            options.putString(LOOKUP_URI_KEY, speedDialObject.getLookup_uri().toString());
            options.putString(NUMBER_KEY, speedDialObject.getNumber());
            appWidgetManager.updateAppWidgetOptions(appWidgetId, options);
        }

        notifySingleTileWidgets(context);
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

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_PHONE_SCHEME);
        builder.appendPath(number);
        intent.setData(builder.build());

        // Package the Intent in a PendingIntent and return it.
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}
