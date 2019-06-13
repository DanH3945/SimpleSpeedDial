package com.danh3945.simplespeeddial.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.QuickContact;
import com.danh3945.simplespeeddial.database.QuickContactDatabase;
import com.danh3945.simplespeeddial.image.ImageHelper;

import java.util.List;

import timber.log.Timber;

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(getApplicationContext());
    }

    class WidgetRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private List<QuickContact> mQuickContactList;
        private QuickContactDatabase mDatabase;

        WidgetRemoteViewsFactory(Context context) {
            this.mContext = context;
            mDatabase = QuickContactDatabase.getQuickContactDatabase(context);
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            mQuickContactList = mDatabase.quickContactDao().getQuickContactList();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            Timber.d("Getting app widget count with value: %s", mQuickContactList.size() > 0 ? mQuickContactList.size() : 0);
            return mQuickContactList.size() > 0 ? mQuickContactList.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Timber.d("Building widget remote view at position: %s", position);

            RemoteViews baseView =
                    new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            QuickContact quickContact = mQuickContactList.get(position);

            Bitmap thumbnail = ImageHelper.getContactPhotoRounded(getApplicationContext(), mQuickContactList.get(position).getLookup_uri());

            baseView.setImageViewBitmap(R.id.widget_image_view, thumbnail);
            baseView.setTextViewText(R.id.widget_item_name_text, quickContact.getName());
            baseView.setTextViewText(R.id.widget_item_number_type_text, quickContact.getNumberType());
            String callUri = "tel:" + quickContact.getNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL);
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
