package com.lkl.lint.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lkl.lint.commonlib.utils.JumpUtils
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

        val num = "dd"
        java.lang.Long.valueOf("123")
        java.lang.Long.parseLong("123")
        "123".toLong()
        java.lang.Long.valueOf("123q")
        "123q".toLong()
        java.lang.Long.valueOf(num)
        num.toLong()
        java.lang.Long.valueOf("123" + "46")
        java.lang.Long.parseLong("123" + "78")

        Integer.valueOf(num)
        Integer.parseInt(num)

        savedInstanceState?.getBoolean("int", true)
        savedInstanceState?.getInt("int", 2)
        savedInstanceState?.getString("string", "")

        val bundle = Bundle()
        bundle.getBoolean("int", false)
        bundle.getInt("int", 2)
        bundle.getString("string", "")

        JumpUtils.safeStartActivity(this, Intent())
        JumpUtils.startActivityForResult(this, Intent(), 2)
    }
}