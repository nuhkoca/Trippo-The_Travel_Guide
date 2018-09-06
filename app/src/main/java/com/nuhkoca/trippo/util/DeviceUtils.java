package com.nuhkoca.trippo.util;

import android.os.Build;

public final class DeviceUtils {

    public static String model() {
        return Build.MODEL;
    }

    public static String device() {
        return Build.DEVICE;
    }

    public static int api() {
        return Build.VERSION.SDK_INT;
    }

    public static String product() {
        return Build.PRODUCT;
    }

    public static String brand() {
        return Build.BRAND;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}