package com.bumbumapps.documentscannerfastest.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.Manifest;

import org.junit.Test;

public class PermissionUtilsTest {
    @Test
    public void modernDevicesOnlyNeedCamera() {
        // Regression: READ_MEDIA_IMAGES/WRITE_EXTERNAL_STORAGE were requested and the success check
        // counted grants off-by-one, so scanning could start without CAMERA or never start.
        assertArrayEquals(new String[]{Manifest.permission.CAMERA}, PermissionUtils.required(true, 35));
    }

    @Test
    public void galleryNeedsNothingOnModernDevices() {
        assertEquals(0, PermissionUtils.required(false, 33).length);
    }

    @Test
    public void legacyDevicesNeedWriteStorage() {
        assertArrayEquals(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PermissionUtils.required(true, 28));
    }

    @Test
    public void allGrantedRequiresEveryResult() {
        assertTrue(PermissionUtils.allGranted(new int[]{0, 0}));
        assertFalse(PermissionUtils.allGranted(new int[]{0, -1}));
        assertFalse(PermissionUtils.allGranted(new int[0]));
    }
}
