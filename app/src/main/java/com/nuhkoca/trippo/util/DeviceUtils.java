package com.nuhkoca.trippo.util;

import android.os.Build;

public class DeviceUtils {

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
}