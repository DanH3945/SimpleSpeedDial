package com.danh3945.simplespeeddial.views;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.views.SingleTileConfig.SingleTileConfigFragment;

public class SingleTileConfigActivity extends ParentActivity {

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

        TextView configText = findViewById(R.id.primary_display_configuring_text_view);
        configText.setText(R.string.primary_display_configuring_single_tile_widget);

        loadFragment(SingleTileConfigFragment.createInstance(finalAppWidgetId), false, SingleTileConfigFragment.TAG);
    }
}
