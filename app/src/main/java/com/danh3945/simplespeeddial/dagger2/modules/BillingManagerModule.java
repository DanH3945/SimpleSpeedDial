package com.danh3945.simplespeeddial.dagger2.modules;

import androidx.appcompat.app.AppCompatActivity;

import com.danh3945.simplespeeddial.billing.BillingManager;

import dagger.Module;
import dagger.Provides;

@Module
public class BillingManagerModule {

    @Provides
    public BillingManager provideBillingManager(AppCompatActivity activity) {
        return new BillingManager(activity);
    }

}
