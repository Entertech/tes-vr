package com.entertech.tes.ble.device

import android.content.Context
import cn.entertech.ble.BaseBleConnectManager
import cn.entertech.ble.uid.device.BaseBluetoothDeviceUuidFactory

class TesDeviceManager(context: Context) : BaseBleConnectManager(context) {


    override fun getBaseBluetoothDeviceUuidFactory(): BaseBluetoothDeviceUuidFactory {
        return TesDeviceUuidFactory
    }
}