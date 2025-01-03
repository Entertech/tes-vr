package com.entertech.tes.ble.device.msg.shakehand

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.DeviceStatus
import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

/**
 * 反馈消息
 * */
class ShakeHandsFbTesMsg : BaseReceiveTesMsg(), IShakeHandFunction {

    companion object {
        const val INDEX_DEVICE_STATUS = 0
        const val INDEX_DEVICE_BATTERY = 1
        const val INDEX_RNG = 2
        const val INDEX_CRC = 3
        private const val TAG = "FeedbackTesMsg"
    }

    var deviceStatus: DeviceStatus? = null
    var deviceBattery: Int = -1
    var rng: Byte?=null
    override fun getMsgLength(): Byte {
        return 0x09
    }


    override fun processMsgData(byteArray: ByteArray): Boolean {
        byteArray.getOrNull(INDEX_DATA_START + INDEX_DEVICE_STATUS)?.apply {
            DeviceStatus.getStatus(this)?.let {
                deviceStatus = it
            } ?: run {
                TesVrLog.e(TAG, "未知状态 $this")
            }
        }

        deviceBattery = byteArray.getOrNull(INDEX_DATA_START + INDEX_DEVICE_BATTERY)?.toInt() ?: -1
        val rng = byteArray.getOrNull(INDEX_DATA_START + INDEX_RNG)
        val crc = byteArray.getOrNull(INDEX_DATA_START + INDEX_CRC)
        return true
    }
}