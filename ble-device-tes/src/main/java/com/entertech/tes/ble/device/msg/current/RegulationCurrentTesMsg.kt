package com.entertech.tes.ble.device.msg.current

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

/**
 * 调节电流
 * */
class RegulationCurrentTesMsg(val current: Byte, val type: Byte) : BaseSendTesMsg(),
    IRegulationCurrentFunction {
    companion object {
        const val INDEX_REGULATION_CURRENT = 1

        /**
         * 电流增加
         * */
        const val REGULATION_CURRENT_INCREASE: Byte = 0xFF.toByte()

        /**
         * 电流减少
         * */
        const val REGULATION_CURRENT_REDUCE: Byte = 0xEE.toByte()
    }

    override fun createDataBytes(byteArray: ByteArray) {
        byteArray[INDEX_DATA_START] = current
        byteArray[INDEX_DATA_START + INDEX_REGULATION_CURRENT] = type
    }

    override fun getMsgLength(): Byte {
        return 0x08
    }

}