package com.entertech.tes.ble.device.msg.shakehand

import com.entertech.tes.ble.device.msg.BaseSendTesMsg
import kotlin.random.Random

/**
 * 握手消息
 * */
class ShakeHandsTesMsg : BaseSendTesMsg(), IShakeHandFunction {
    companion object {
        const val INDEX_RNG = 0
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

    override fun createDataBytes(byteArray: ByteArray) {
        byteArray[INDEX_DATA_START + INDEX_RNG] = generateRandomByte()
    }

    private fun generateRandomByte(): Byte {
        val invalidNumbers = setOf(0xFF.toByte(), 0xFE.toByte())
        var randomByte: Byte
        do {
            randomByte = Random.nextInt(0, 0xFF).toByte() // 生成范围为 0x00 到 0xFE 的随机数，并转为 Byte
        } while (randomByte in invalidNumbers)
        return randomByte
    }
}