package com.grod.one;

import android.app.Application;

public class OneApp extends Application {
    public static OneApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }


}
