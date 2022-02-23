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
        void onOk();
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CODE_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (listener != null) {
                    listener.onOk();
                    listener = null;
                }
            }
        }
    }

    public static void request(Activity act, String[] permissions, OnPermissListener l) {
        listener = l;
        boolean hasPer = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(act,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {
                hasPer = false;
                break;
            }
        }
        if (hasPer) {
            l.onOk();
            listener = null;
        } else {
            ActivityCompat.requestPermissions(act,
                    permissions, CODE_REQUEST);
        }
    }

    public static String[] permissFile() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    public static String[] permissCamera() {
        return new String[]{Manifest.permission.CAMERA};
    }
}
