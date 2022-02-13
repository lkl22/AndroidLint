package com.lkl.lint.commonlib.utils;

import android.content.Intent;

public class SafeIntent extends Intent {
    public SafeIntent(Intent o) {
        super(o);
    }

    public boolean getBooleanExtra(String name, boolean defaultValue) {
        try {
            return super.getBooleanExtra(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
