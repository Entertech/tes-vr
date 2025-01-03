package com.entertech.tes.ble.device.msg.set

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

/**
 * 设置参数消息
 * */
class SettingArgTesMsg(private val modeType:Byte,val current:Byte=0x00,val time:Byte,val frequency:ByteArray): BaseSendTesMsg(),ISetFunction {
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
        byteArray[INDEX_DATA_START + INDEX_SETTING_MODE] = modeType
        byteArray[INDEX_DATA_START + INDEX_SETTING_CURRENT] = current
        byteArray[INDEX_DATA_START + INDEX_SETTING_TIME] = time
        frequency.forEachIndexed { index, byte ->
            byteArray[INDEX_DATA_START + INDEX_SETTING_FREQUENCY + index] = byte
        }
    }

    override fun hasCrcData(): Boolean {
        return false
    }
}