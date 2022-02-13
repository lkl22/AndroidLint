package com.lkl.lint.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lkl.lint.demo.R.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        Log.d(TAG, "test")
        android.util.Log.d(TAG, "test")
        println(android.util.Log.d(TAG, "test"))

        val intentVal = intent
        intentVal.getBooleanExtra ( "dd", true)
        intentVal
            .getBooleanExtra ( "dd", true)
        getIntent().getBooleanExtra("dd", true)
        getIntent()
            .getBooleanExtra("dd", true)
        intent.getBooleanExtra ("dd", true)

        intent
            .getBooleanExtra ("dd", true)

        println(intent
            .getBooleanExtra ("dd", true))

        intent.getIntExtra ("dd", 1)
    }
}