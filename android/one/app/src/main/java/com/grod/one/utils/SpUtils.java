package com.grod.one.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.grod.one.OneApp;

public class SpUtils {


    private static SharedPreferences spf() {
        return OneApp.app.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static void putString(String key, String data) {
        spf().edit().putString(key, data).apply();
    }

    public static String getString(String key) {
        return spf().getString(key, "");
    }

    public static void putInt(String key, int data) {
        spf().edit().putInt(key, data).apply();
    }

    public static int getInt(String key) {
        return spf().getInt(key, 0);
    }
    public static int getInt(String key,int def) {
        return spf().getInt(key, def);
    }

    public static void putBoolean(String key, boolean data) {
        spf().edit().putBoolean(key, data).apply();
    }

    public static boolean getBoolean(String key) {
        return spf().getBoolean(key, false);
    }
}
