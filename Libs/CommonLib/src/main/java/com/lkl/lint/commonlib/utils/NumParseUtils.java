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
}
