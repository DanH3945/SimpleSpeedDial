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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Timber.d("Single Tile Widget - Called onUpdate");
        for (int appWidgetId : appWidgetIds) {
            Timber.d("Updating app widget with ID: %s", appWidgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_layout);

            String number = appWidgetManager.getAppWidgetOptions(appWidgetId).getString(NUMBER_KEY);

            if (number != null) {
                Timber.d("Single Tile Widget - number was NOT NULL.  Setting pending intent.");
                PendingIntent pendingIntent = getDialPendingIntent(context, number);

                views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);
            } else {
                Timber.d("Single Tile Widget - number was NULL");
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    public static void setupSingleTileWidget(Context context, int appWidgetId, SpeedDialObject speedDialObject) {
        Timber.d("Setting up widget with ID: %s", appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_single_layout);

        views.setTextViewText(R.id.widget_single_text, speedDialObject.getName());

        Bitmap bitmap = ImageHelper.getContactPhotoRounded(context, speedDialObject.getLookup_uri());

        views.setImageViewBitmap(R.id.widget_single_image, bitmap);

        PendingIntent pendingIntent = getDialPendingIntent(context, speedDialObject.getNumber());
        views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options != Bundle.EMPTY) {
            options.putString(NUMBER_KEY, speedDialObject.getNumber());
            appWidgetManager.updateAppWidgetOptions(appWidgetId, options);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static PendingIntent getDialPendingIntent(Context context, String number) {

        Intent intent;
        if (InstantDial.shouldInstantDial(context)) {
            intent = new Intent(Intent.ACTION_CALL);
        } else {
            intent = new Intent(Intent.ACTION_DIAL);
        }

        String callUri = "tel:" + number;
        intent.setData(Uri.parse(callUri));


        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public static void notifySingleTileWidgets(Context context) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName componentName = new ComponentName(context, SingleTileAppWidgetProvider.class.getName());

        int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);

        Intent intent = new Intent(context, SingleTileAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        context.sendBroadcast(intent);
    }

}
