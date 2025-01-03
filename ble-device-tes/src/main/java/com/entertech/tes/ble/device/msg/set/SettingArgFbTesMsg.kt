package com.entertech.tes.ble.device.msg.set

import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

/**
 * 设置参数反馈消息
 * */
class SettingArgFbTesMsg : BaseReceiveTesMsg(),ISetFunction {

    companion object {
        /**
         * 0x01:成功、0x02:失败、0x03:参数错误
         * */
        const val INDEX_SETTING_ARG_RESULT = 0
        const val SETTING_ARG_RESULT_SUCCESS: Byte = 0x01
        const val SETTING_ARG_RESULT_FAILURE: Byte = 0x02
        const val SETTING_ARG_RESULT_ERROR: Byte = 0x03
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

    override fun processMsgData(byteArray: ByteArray): Boolean {
        val result = byteArray.getOrNull(INDEX_DATA_START + INDEX_SETTING_ARG_RESULT)
        when (result) {

        }
        return true
    }

}