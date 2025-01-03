package com.entertech.tes.ble.device.msg.control

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

class ControlCommandFdTesMsg : BaseReceiveTesMsg(), IControlCommandFunction {

    companion object {
        private const val TAG = "ControlCommandFdTesMsg"

        /**
         * DATA[0]：设备启动反馈：0x01:启动成功 0x02:启动失败
         * DATA[0]：设备断开连接反馈：0x01:断开连接成功 0x02:断开连接失败
         * */
        const val INDEX_CONTROL_RESULT = 0
        const val CONTROL_RESULT_SUCCESS: Byte = 0x01
        const val CONTROL_RESULT_FAILURE: Byte = 0x02
    }

    override fun processMsgData(byteArray: ByteArray): Boolean {
        when (val controlResult = byteArray.getOrNull(INDEX_DATA_START + INDEX_CONTROL_RESULT)) {
            CONTROL_RESULT_SUCCESS -> {
                //启动成功
                TesVrLog.d(TAG, "控制指令成功")
            }

            CONTROL_RESULT_FAILURE -> {
                //启动失败
                TesVrLog.d(TAG, "控制指令失败")
            }

            else -> {
                TesVrLog.d(TAG, "控制指令未知错误 $controlResult")
            }
        }
        return true
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

}