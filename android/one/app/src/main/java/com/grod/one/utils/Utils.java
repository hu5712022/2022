package com.grod.one.utils;

import android.os.Environment;
import android.util.Log;

import com.grod.one.OneApp;

public class Utils {


    public static int color(int res) {
        return OneApp.app.getResources().getColor(res);
    }


    public static void logE(String data) {
        Log.e("hy", data);
    }

    public static String cachePath() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = OneApp.app.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = OneApp.app.getCacheDir().getPath();
        }
        return cachePath;

    }
}
