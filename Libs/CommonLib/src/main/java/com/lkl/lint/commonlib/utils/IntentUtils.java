package com.lkl.lint.commonlib.utils;

import android.annotation.SuppressLint;
import android.content.Intent;

public class IntentUtils {
    @SuppressLint("IntentUsage")
    public static boolean getBooleanExtra(Intent intent, String name, boolean defaultValue) {
        try {
            return intent.getBooleanExtra(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @SuppressLint("IntentUsage")
    public static boolean safeGetBooleanExtra(Intent intent, String name, boolean defaultValue) {
        try {
            return intent.getBooleanExtra(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @SuppressLint("IntentUsage")
    public static int getIntExtra(Intent intent, String name, int defaultValue) {
        try {
            return intent.getIntExtra(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
