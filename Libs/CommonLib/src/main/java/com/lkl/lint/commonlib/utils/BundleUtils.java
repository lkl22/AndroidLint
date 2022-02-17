package com.lkl.lint.commonlib.utils;

import android.os.Bundle;

public class BundleUtils {

    public static boolean safeGetBoolean(Bundle bundle, String name, boolean defaultValue) {
        try {
            return bundle.getBoolean(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static int getInt(Bundle bundle, String name, int defaultValue) {
        try {
            return bundle.getInt(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static String getString(Bundle bundle, String name, String defaultValue) {
        try {
            return bundle.getString(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
