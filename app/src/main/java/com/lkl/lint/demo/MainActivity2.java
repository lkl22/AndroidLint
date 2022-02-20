package com.lkl.lint.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lkl.lint.commonlib.utils.JumpUtils;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Log.d(TAG, "test");
        android.util.Log.d(TAG, "test");
        System.out.println(android.util.Log.d(TAG, "test"));

        Intent intent = getIntent();
        intent.getBooleanExtra("dd",
                true);
        intent
                .getBooleanExtra
                        ("dd",
                                true);
        getIntent().getBooleanExtra("dd",
                true);
        getIntent()
                .getBooleanExtra
                        ("dd",
                                true);

        getIntent().getIntExtra("dd", 2);

        String num = "dd";
        Long.valueOf("123");
        Long.parseLong("123");
        Long.valueOf("123q");
        Long.parseLong("123q");
        Long.valueOf(num);
        Long.parseLong(num);
        Long.valueOf("123" + "46");
        Long.parseLong("123" + "78");

        Integer.valueOf(num);
        Integer.parseInt(num);

        savedInstanceState.getBoolean("int", false);
        savedInstanceState.getInt("int", 2);
        savedInstanceState.getString("string", "");

        Bundle bundle = new Bundle();
        bundle.getBoolean("int", false);
        bundle.getInt("int", 2);
        bundle.getString("string", "");

        JumpUtils.safeStartActivity(this, new Intent());
        JumpUtils.startActivityForResult(this, new Intent(), 3);
    }
}