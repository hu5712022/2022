package com.grod.one.utils;

import android.util.Log;

import com.grod.one.OneApp;

public class Utils {


    public static int color(int res){
       return OneApp.app.getResources().getColor(res);
    }


    public static void logE(String data){
        Log.e("hy",data);
    }
}
