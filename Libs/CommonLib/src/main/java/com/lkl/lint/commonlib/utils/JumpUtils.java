package com.lkl.lint.commonlib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class JumpUtils {
    public static boolean safeStartActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean startActivityForResult(Activity activity, Intent intent, int request) {
        try {
            activity.startActivityForResult(intent, request);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
