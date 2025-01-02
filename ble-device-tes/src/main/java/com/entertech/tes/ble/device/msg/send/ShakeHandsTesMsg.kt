package com.entertech.tes.ble.device.msg.send

import kotlin.random.Random

/**
 * 握手消息
 * */
class ShakeHandsTesMsg: BaseSendTesMsg() {
    companion object{
        const val INDEX_RNG = 0
        const val INDEX_CRC = 1
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

    override fun getCommandByte(): Byte {
        return 0x00
    }

    override fun createDataBytes(byteArray: ByteArray) {
        byteArray[INDEX_DATA_START + INDEX_RNG] = generateRandomByte()
        byteArray[INDEX_DATA_START + INDEX_CRC] = calculateCRC(byteArray)
    }

    private fun calculateCRC( bytes: ByteArray): Byte {
        val sum = bytes.sumOf { it.toUByte().toInt() } // 计算总和，确保无符号加法
        return (sum and 0xFF).toByte() // 只保留低 8 位
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