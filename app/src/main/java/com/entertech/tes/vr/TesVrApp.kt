package com.entertech.tes.vr

import android.app.Application
import cn.entertech.base.util.DateUtils
import cn.entertech.log.local.StorageLogPrinter
import com.entertech.tes.ble.TesVrLog
import java.util.Date

class TesVrApp : Application() {
    private val simpleDateFormat by lazy {
        DateUtils.getDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    }

    companion object {
        lateinit var instance: TesVrApp
    }

    override fun onCreate() {
        super.onCreate()
        TesVrLog.setBasePrinter(
            StorageLogPrinter(
                this,
                dataPrefix = { "${getCurrentTimeString()}=> " },
            )
        )
        instance = this
    }

    private fun getCurrentTimeString() = simpleDateFormat?.format(Date()) ?: ""
}