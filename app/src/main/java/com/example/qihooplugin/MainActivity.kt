package com.example.qihooplugin

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.qihoo360.replugin.RePlugin

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openPlugin()
    }

    private fun openPlugin() {
        val intent = Intent()
        intent.component = ComponentName(
            "android_webView",
            "com.example.msi.platformforup.WelcomeActivity"
        )
        RePlugin.startActivity(this@MainActivity, intent)
    }



}