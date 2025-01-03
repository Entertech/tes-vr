package com.entertech.tes.ble.device

import android.content.Context
import cn.entertech.ble.BaseBleConnectManager
import cn.entertech.ble.uid.device.BaseBluetoothDeviceUuidFactory

class TesDeviceManager(context: Context) : BaseBleConnectManager(context) {

    var deviceStatus: DeviceStatus? = null
    var battery: Int = -1

    override fun getBaseBluetoothDeviceUuidFactory(): BaseBluetoothDeviceUuidFactory {
        return TesDeviceUuidFactory
    }
}