package com.hereticpurge.simplespeeddial.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.hereticpurge.simplespeeddial.R;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent serviceIntent = new Intent(context, WidgetRemoteViewsService.class);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.empty_view);

            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    public static void notifyWidgets(Context context) {
        // Notifying the app widget that some information has changed and it should update.
        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(context);

        ComponentName appWidget = new ComponentName(context, WidgetProvider.class.getName());

        int[] widgetIds = appWidgetManager.getAppWidgetIds(appWidget);

        appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widget_list_view);
    }
}
