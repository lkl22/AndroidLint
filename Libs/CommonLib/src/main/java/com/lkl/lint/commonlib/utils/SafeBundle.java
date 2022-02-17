package com.lkl.lint.commonlib.utils;

import android.os.Bundle;

public class SafeBundle {
    private Bundle bundle;

    public SafeBundle(Bundle o) {
        if (o == null) {
            bundle = new Bundle();
        } else {
            bundle = o;
        }

    }

    public boolean getBoolean(String name, boolean defaultValue) {
        try {
            return bundle.getBoolean(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public int getInt(String name, int defaultValue) {
        try {
            return bundle.getInt(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public String getString(String name, String defaultValue) {
        try {
            return bundle.getString(name, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
