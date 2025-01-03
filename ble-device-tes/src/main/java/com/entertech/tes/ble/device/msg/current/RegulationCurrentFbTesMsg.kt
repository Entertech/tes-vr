package com.entertech.tes.ble.device.msg.current

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

class RegulationCurrentFbTesMsg : BaseReceiveTesMsg(), IRegulationCurrentFunction {

    companion object {
        private const val TAG = "RegulationCurrentFbTesMsg"

        /**
         * 0xFF:电流增加 0XEE:电流减小
         * */
        const val INDEX_REGULATION_TYPE = 1

        /**
         * 0x00:成功 0X01:剩余时间不足，无法调至到此模式；0x02:设备正在缓降阶段无法 调节电流;0x03:失败
         * */
        const val INDEX_REGULATION_RESULT = 2

        const val REGULATION_RESULT_SUCCESS: Byte = 0x00
        const val REGULATION_RESULT_TIME_NOT_ENOUGH: Byte = 0x01
        const val REGULATION_RESULT_DEVICE_IN_REDUCE: Byte = 0x02
        const val REGULATION_RESULT_FAILURE: Byte = 0x03
    }

    override fun processMsgData(byteArray: ByteArray): Boolean {
        val regulationType = byteArray.getOrNull(INDEX_DATA_START + INDEX_REGULATION_TYPE)
        val regulationResult = byteArray.getOrNull(INDEX_DATA_START + INDEX_REGULATION_RESULT)
        when (regulationResult) {
            REGULATION_RESULT_SUCCESS -> {
                //调节电流成功
                TesVrLog.d(TAG, "调节电流成功")
            }

            REGULATION_RESULT_TIME_NOT_ENOUGH -> {
                TesVrLog.d(TAG, "剩余时间不足，无法调至到此模式")
            }

            REGULATION_RESULT_DEVICE_IN_REDUCE -> {
                TesVrLog.d(TAG, "设备正在缓降阶段无法 调节电流")
            }

            REGULATION_RESULT_FAILURE -> {
                TesVrLog.d(TAG, "失败")
            }
        }
        return true
    }

    override fun getMsgLength(): Byte {
        return 0x09
    }

}