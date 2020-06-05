package com.multilingual.firebase.chat.activities;

import android.app.Application;

import com.multilingual.firebase.chat.activities.fcm.ApplicationLifecycleManager;
import com.google.android.gms.ads.MobileAds;

public class UIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());
    }
}
