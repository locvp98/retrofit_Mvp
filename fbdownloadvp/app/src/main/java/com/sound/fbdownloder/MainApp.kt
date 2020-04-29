package com.sound.fbdownloder

import android.app.Application
import com.orhanobut.hawk.Hawk

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
    }
}