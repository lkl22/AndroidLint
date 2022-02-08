package com.lkl.androidlint.utils;

import android.annotation.SuppressLint;
import android.util.Log;

public class LogUtils {
    @SuppressLint("LogUsage")
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }
}
