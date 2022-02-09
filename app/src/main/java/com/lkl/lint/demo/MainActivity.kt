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
    }
}