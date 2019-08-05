package com.danh3945.simplespeeddial.views;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.views.contactList.ContactListFragment;
import com.danh3945.simplespeeddial.views.contactList.ContactListRecyclerAdapter;
import com.danh3945.simplespeeddial.widget.SingleTileAppWidgetProvider;

import timber.log.Timber;

public class SingleTileConfigActivity extends MobileAdsActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // Unusually we're calling setContentView before the super call to onCreate.  This is so
        // the mobile ads in the parent class has something to bind to.  The parent class then calls
        // the actual AppCompatActivity super class.
        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // Make the id final so it can be used below.
        int finalAppWidgetId = appWidgetId;
        ContactListRecyclerAdapter.ContactListResultCallback callback = new ContactListRecyclerAdapter.ContactListResultCallback() {
            @Override
            public void clickResult(SpeedDialObject object) {
                // Todo configure the single tile widget with the result information.
                Timber.d("Configuring single tile widget with name: %s, number: %s, numberType: %s",
                        object.getName(), object.getNumber(), object.getNumberType());

                SingleTileAppWidgetProvider.setupSingleTileWidget(SingleTileConfigActivity.this, finalAppWidgetId, object);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, finalAppWidgetId);
                SingleTileConfigActivity.this.setResult(RESULT_OK, resultValue);
                finish();
            }
        };

        loadFragment(ContactListFragment.createInstanceForResult(callback), true, ContactListFragment.TAG);
    }
}
