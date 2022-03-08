package com.grod.one.utils;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.grod.one.OneApp;

import java.io.File;

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
            cachePath = "/sdcard/1/";
            File file = new File(cachePath);
            if(!file.exists()){
                file.mkdir();
            }
        } else {
            //外部存储不可用
            cachePath = OneApp.app.getCacheDir().getPath();
        }
        return cachePath;

    }

    public static void toast(String msg) {
        Toast.makeText(OneApp.app,msg,Toast.LENGTH_SHORT).show();
    }
}
