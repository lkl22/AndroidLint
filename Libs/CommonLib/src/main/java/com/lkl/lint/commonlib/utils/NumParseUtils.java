package com.lkl.lint.commonlib.utils;

import android.annotation.SuppressLint;

public class NumParseUtils {
    @SuppressLint("NumParseUsage")
    public static long parseLong(String data, long defaultValue) {
        try {
            return Long.parseLong(data);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @SuppressLint("NumParseUsage")
    public static int safeParseInt(String data, int defaultValue) {
        try {
            return Integer.parseInt(data);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
