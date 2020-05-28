package com.quan.hineighboorguide2;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;
import com.ijvpbsdiwc.adx.service.AdsExchange;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
        AdsExchange.init(this, "");
    }
}