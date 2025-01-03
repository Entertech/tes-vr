package com.entertech.tes.ble.device.msg.set

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

/**
 * 设置参数消息
 * */
class SettingArgTesMsg: BaseSendTesMsg(),ISetFunction {
    companion object{
        /**
         * 模式设置
         * */
        private const val INDEX_SETTING_MODE = 0
        /**
         * 电流设置
         * */
        private const val INDEX_SETTING_CURRENT = 1
        /**
         * 电流设置
         * */
        private const val INDEX_SETTING_TIME = 2
        /**
         * 频率设置
         * */
        private const val INDEX_SETTING_FREQUENCY = 3


    }


    override fun getMsgLength(): Byte {
        return 0x0B
    }

    override fun createDataBytes(byteArray: ByteArray) {

    }

    /**
     * int转成16进制的小端字节数组
     * */
    private fun intToLittleEndianBytes(value: Int): ByteArray {
        return ByteArray(4) { index ->
            (value shr (index * 8) and 0xFF).toByte()
        }
    }
}