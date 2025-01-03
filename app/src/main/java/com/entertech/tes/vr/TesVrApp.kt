package com.entertech.tes.vr

import android.app.Application

class TesVrApp : Application() {

    companion object {
        lateinit var instance: TesVrApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}