package com.grod.one;

import android.app.Application;

import com.cyl.musicapi.BaseApiImpl;

public class OneApp extends Application {
    public static OneApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        BaseApiImpl.INSTANCE.initWebView(this);
    }


}
