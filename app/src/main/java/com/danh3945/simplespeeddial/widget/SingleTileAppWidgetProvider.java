package com.danh3945.simplespeeddial.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.LargeWidgetObject;
import com.danh3945.simplespeeddial.database.SingleTileWidgetObject;
import com.danh3945.simplespeeddial.database.SpeedDialDatabase;
import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.views.preferences.InstantDial;

import timber.log.Timber;

public class SingleTileAppWidgetProvider extends AppWidgetProvider {

    private static final String URI_PHONE_SCHEME = "tel";
    public static final String SINGLE_TILE_TEXT_COLOR_KEY = "singleTileTextColorKey";

    @Override
    public void onEnabled(Context context) {
        notifySingleTileWidgets(context);
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            SingleTileWidgetObject singleTileWidgetObject = new SingleTileWidgetObject();
            singleTileWidgetObject.setWidgetId(appWidgetId);
            singleTileWidgetObject.removeFromDatabase(context);
        }

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Get and update each of the single tile widgets associated with this provider.

                // Getting the color the user has defined for the color of the widget text.  Or the
                // default if the user hasn't defined a text color.
                int color = getWidgetTextColor(context);

                Timber.d("Single Tile Widget - Called onUpdate");
                for (int appWidgetId : appWidgetIds) {
                    Timber.d("Updating app widget with ID: %s", appWidgetId);

                    SingleTileWidgetObject[] singleTileWidgetObjects =
                            SpeedDialDatabase
                                    .getSpeedDialDatabase(context)
                                    .singleTileWidgetDao()
                                    .getSingleTileWidget(appWidgetId);

                    // Making sure we got something from the database
                    if (singleTileWidgetObjects.length < 1) {
                        continue;
                    }

                    SingleTileWidgetObject widgetObject = singleTileWidgetObjects[0];

                    // Define the view for each widget.
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_layout);

                    // Update the pending intent for the number to reflect preferences set by the user.
                    if (widgetObject.getNumber() != null) {
                        PendingIntent pendingIntent = getDialPendingIntent(context, widgetObject.getNumber());

                        views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);
                    } else {
                        Timber.d("Number was null for widget %s, skipping", appWidgetId);
                    }

                    // As long as the name isn't null set the name value to the one associated with the widget.
                    // If it is null the default value of the TextView in res will remain.
                    if (widgetObject.getName() != null) {
                        views.setTextViewText(R.id.widget_single_name_text, widgetObject.getName());
//                        views.setTextColor(R.id.widget_single_name_text, color);
                    } else {
                        Timber.d("Name was null for widget %s, skipping", appWidgetId);
                    }

                    // Setting the number type text
                    if (widgetObject.getNumberType() != null && !widgetObject.getNumberType().equals("")) {
                        views.setTextViewText(R.id.widget_single_number_type_text, widgetObject.getNumberType());
//                        views.setTextColor(R.id.widget_single_number_type_text, color);
                    } else {
                        Timber.d("Number type was null for widget %s, skipping", appWidgetId);
                    }

                    // Get the lookupUri and use it to retrieve the current user thumbnail and apply
                    // it to the widget.  Otherwise use the ImageHelper default image.
                    int dimen = (int) context.getResources().getDimension(R.dimen.widget_large_text_drawable_image_dimensions);
                    Bitmap bitmap = ImageHelper.getContactPhoto(context,
                            widgetObject.getLookupUri(),
                            widgetObject.getName(),
                            dimen,
                            dimen);
                    views.setImageViewBitmap(R.id.widget_single_image, bitmap);

                    // Our changes to the widgets are done so we send off the updates to the widget manager
                    // so it can complete its updates.
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        });

    }

    public static void notifySingleTileWidgets(Context context) {

        // Grab all the current Widgets associated with this Provider and tell them to update their
        // state.  Eventually calls the OnUpdate function above.

        int[] widgetIds = getActiveWidgetIds(context);

        Intent intent = new Intent(context, SingleTileAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        context.sendBroadcast(intent);
    }

    public static int[] getActiveWidgetIds(Context context) {
        ComponentName componentName = new ComponentName(context, SingleTileAppWidgetProvider.class.getName());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
    }

    public static void setupFromConfigurationActivity(Context context, int appWidgetId, LargeWidgetObject largeWidgetObject) {

        // Called by any configuration activity to setup a single tile widget with the parameters
        // in the LargeWidgetObject.

        // We save a default color here and bind it to the widget options itself so that
        // the color is always the same for this particular widget to avoid confusing
        // the user.
//        int defaultColor = ImageHelper.getRandomContactIconColorInt(context);

        SingleTileWidgetObject singleTileWidgetObject =
                SingleTileWidgetObject.fromLargeWidgetObject(appWidgetId, largeWidgetObject);

        singleTileWidgetObject.addToDatabase(context);

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

    public static void setWidgetTextColor(Context context, int color) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(SINGLE_TILE_TEXT_COLOR_KEY, color).apply();
        notifySingleTileWidgets(context);
    }

    private int getWidgetTextColor(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(SINGLE_TILE_TEXT_COLOR_KEY, Color.WHITE);
    }

}
