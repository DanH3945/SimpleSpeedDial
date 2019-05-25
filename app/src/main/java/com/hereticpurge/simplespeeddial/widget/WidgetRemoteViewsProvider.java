package com.hereticpurge.simplespeeddial.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hereticpurge.simplespeeddial.R;
import com.hereticpurge.simplespeeddial.database.QuickContact;
import com.hereticpurge.simplespeeddial.database.QuickContactDatabase;

import java.util.List;

public class WidgetRemoteViewsProvider extends RemoteViewsService {

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
            mDatabase.close();
        }

        @Override
        public int getCount() {
            return mQuickContactList.size() > 0 ? mQuickContactList.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews baseView =
                    new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            QuickContact quickContact = mQuickContactList.get(position);

            baseView.setTextViewText(R.id.widget_item_name_text, quickContact.getName());
            baseView.setTextViewText(R.id.widget_item_number_text, quickContact.getNumber());

            String callUri = "tel:" + quickContact.getNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(callUri));

            baseView.setOnClickFillInIntent(R.id.widget_item_card, intent);

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
