package com.grod.one.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final int CODE_REQUEST = 1;
    private static OnPermissListener listener;

    public interface OnPermissListener {

    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CODE_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    public static void request(Activity act, String[] permissions, OnPermissListener l) {
        listener = l;

        if (ContextCompat.checkSelfPermission(act,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(act,
                        permissions, CODE_REQUEST);
            } else {
                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        CODE_REQUEST);

            }
        }
    }

}
