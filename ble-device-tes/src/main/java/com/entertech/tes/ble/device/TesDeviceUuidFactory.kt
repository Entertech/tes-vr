package com.entertech.tes.ble.device

import cn.entertech.ble.uid.device.BaseBluetoothDeviceUuidFactory

object TesDeviceUuidFactory: BaseBluetoothDeviceUuidFactory() {
    override fun getBroadcastUUid(): String {
        return "0003cdd0-0000-1000-8000-00805f9b0131"
    }
}