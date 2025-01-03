package com.entertech.tes.ble.device.msg.shakehand

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

/**
 * 反馈消息
 * */
class ShakeHandsFbTesMsg : BaseReceiveTesMsg(),IShakeHandFunction {

    companion object {
        const val INDEX_DEVICE_STATUS = 0
        const val INDEX_DEVICE_BATTERY = 1
        const val INDEX_RNG = 2
        const val INDEX_CRC = 3
        private const val TAG = "FeedbackTesMsg"
        const val DEVICE_STATUS_UN_KNOW: Int = -1
        const val DEVICE_STATUS_RUNNING: Int = 1
        const val DEVICE_STATUS_READY: Int = 2
        const val DEVICE_STATUS_ERROR: Int = 3

    }

    override fun getMsgLength(): Byte {
        return 0x09
    }


    override fun processMsgData(byteArray: ByteArray): Boolean {
        val deviceStatus = byteArray.getOrNull(INDEX_DATA_START + INDEX_DEVICE_STATUS)
        when (deviceStatus) {
            0x01.toByte() -> {
                TesVrLog.d(TAG, "设备运行中")
            }

            0x02.toByte() -> {
                TesVrLog.d(TAG, "设备就绪中")
            }

            0x03.toByte() -> {
                TesVrLog.d(TAG, "设备故障")
            }
            else->{
                TesVrLog.d(TAG, "未知状态 $deviceStatus")
            }
        }
        val deviceBattery = byteArray.getOrNull(INDEX_DATA_START + INDEX_DEVICE_BATTERY)?.toInt()
        TesVrLog.d(TAG, "设备电量 $deviceBattery")
        val rng = byteArray.getOrNull(INDEX_DATA_START + INDEX_RNG)
        val crc = byteArray.getOrNull(INDEX_DATA_START + INDEX_CRC)
        return true
    }
}