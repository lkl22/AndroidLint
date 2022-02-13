package com.lkl.lint.commonlib.utils;

import android.annotation.SuppressLint;
import android.content.Intent;

public class SafeIntent extends Intent {
    public SafeIntent(Intent o) {
        super(o);
    }

    @SuppressLint("IntentUsage")
    public boolean getBooleanExtra(String name, boolean defaultValue) {
        try {
            return super.getBooleanExtra(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @SuppressLint("IntentUsage")
    public int getIntExtra(String name, int defaultValue) {
        try {
            return super.getIntExtra(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
