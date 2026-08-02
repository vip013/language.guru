package com.example

import android.app.Application
import com.example.data.AdMobHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AdMobHelper.initialize(this)
    }
}
