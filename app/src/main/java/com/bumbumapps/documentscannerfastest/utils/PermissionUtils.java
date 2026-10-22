package com.bumbumapps.documentscannerfastest.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime permissions actually needed: CAMERA for scanning, WRITE_EXTERNAL_STORAGE only up to API 28
 * (PDFs are written to Download/). Gallery import uses the system photo picker and needs no permission.
 */
public final class PermissionUtils {

    private PermissionUtils() {
    }

    public static String[] required(boolean needCamera, int sdkInt) {
        List<String> list = new ArrayList<>();
        if (needCamera) {
            list.add(Manifest.permission.CAMERA);
        }
        if (sdkInt <= Build.VERSION_CODES.P) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return list.toArray(new String[0]);
    }

    public static String[] missing(Activity activity, boolean needCamera) {
        List<String> list = new ArrayList<>();
        for (String p : required(needCamera, Build.VERSION.SDK_INT)) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }
        }
        return list.toArray(new String[0]);
    }

    public static boolean allGranted(int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            return false;
        }
        for (int r : grantResults) {
            if (r != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
