package com.danh3945.simplespeeddial.dagger2;

import com.danh3945.simplespeeddial.billing.BillingManager;
import com.danh3945.simplespeeddial.dagger2.modules.ActivityModule;
import com.danh3945.simplespeeddial.dagger2.modules.BillingManagerModule;

import dagger.Component;

@Component (modules = {BillingManagerModule.class, ActivityModule.class})
public interface BillingComponent {

    BillingManager getBillingManager();

}
