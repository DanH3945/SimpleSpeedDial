package com.danh3945.simplespeeddial.views;

import android.os.Bundle;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.views.primaryDisplay.PrimaryDisplayFragment;

public class MainDisplayActivity extends SimpleSpeedDialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Unusually we're calling setContentView before the super call to onCreate.  This is so
        // the mobile ads in the parent class has something to bind to.  The parent class then calls
        // the actual AppCompatActivity super class.
        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);

        // load the main fragment to display to the user.
        loadFragment(PrimaryDisplayFragment.createInstance(), false, PrimaryDisplayFragment.TAG);
    }

}
