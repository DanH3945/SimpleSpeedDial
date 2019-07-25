package com.danh3945.simplespeeddial.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialDatabase;
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.views.preferences.InstantDial;

import java.util.List;

import timber.log.Timber;

public class LargeWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(getApplicationContext());
    }

    class WidgetRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private List<SpeedDialObject> mSpeedDialObjectList;
        private SpeedDialDatabase mDatabase;

        WidgetRemoteViewsFactory(Context context) {
            this.mContext = context;
            mDatabase = SpeedDialDatabase.getSpeedDialDatabase(context);
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            mSpeedDialObjectList = mDatabase.speedDialDao().getSpeedDialButtonsList();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            Timber.d("Getting app widget count with value: %s", mSpeedDialObjectList.size() > 0 ? mSpeedDialObjectList.size() : 0);
            return mSpeedDialObjectList.size() > 0 ? mSpeedDialObjectList.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Timber.d("Building widget remote view at position: %s", position);

            RemoteViews baseView =
                    new RemoteViews(mContext.getPackageName(), R.layout.widget_large_item);

            SpeedDialObject speedDialObject = mSpeedDialObjectList.get(position);

            Bitmap thumbnail = mSpeedDialObjectList
                    .get(position)
                    .getContactPhotoRounded(mContext);

            baseView.setImageViewBitmap(R.id.widget_image_view, thumbnail);
            baseView.setTextViewText(R.id.widget_item_name_text, speedDialObject.getName());
            baseView.setTextViewText(R.id.widget_item_number_type_text, speedDialObject.getNumberType());
            String callUri = "tel:" + speedDialObject.getNumber();

            Intent intent;

            if (InstantDial.shouldInstantDial(getApplicationContext())) {
                Timber.d("Making instant call");
                intent = new Intent(Intent.ACTION_CALL);
            } else {
                Timber.d("Making regular call");
                intent = new Intent(Intent.ACTION_DIAL);
            }

            intent.setData(Uri.parse(callUri));

            baseView.setOnClickFillInIntent(R.id.widget_item_card, intent);

            Timber.d("Finished Building view, returning.");
            return baseView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}
