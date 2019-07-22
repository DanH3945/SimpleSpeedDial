package com.danh3945.simplespeeddial.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.prefs.InstantDial;

import timber.log.Timber;

public class SingleTileAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    public static void setupSingleTileWidget(Context context, int appWidgetId, SpeedDialObject speedDialObject) {

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_single_layout);

        Intent intent;
        if (InstantDial.shouldInstantDial(context)) {
            Timber.d("Making instant call");
            intent = new Intent(Intent.ACTION_CALL);
        } else {
            Timber.d("Making regular call");
            intent = new Intent(Intent.ACTION_DIAL);
        }

        String callUri = "tel:" + speedDialObject.getNumber();
        intent.setData(Uri.parse(callUri));

        views.setTextViewText(R.id.widget_single_text, speedDialObject.getName());

        Bitmap bitmap = ImageHelper.getContactPhotoRounded(context, speedDialObject.getLookup_uri());

        views.setImageViewBitmap(R.id.widget_single_image, bitmap);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_single_tile_base_layout, pendingIntent);

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }

}
