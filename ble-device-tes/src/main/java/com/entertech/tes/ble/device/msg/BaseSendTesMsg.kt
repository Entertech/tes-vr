package com.entertech.tes.ble.device.msg

/**
 * app 发送的消息
 * */
abstract class BaseSendTesMsg : BaseTesMsg() {

    fun createMsg(): ByteArray {
        val byteArray = ByteArray(getMsgLength().toInt())
        byteArray[INDEX_PACKET_HEAD_1] = PACKET_HEAD_1
        byteArray[INDEX_PACKET_HEAD_2] = PACKET_HEAD_2
        byteArray[INDEX_INDEX] = PACKET_INDEX
        byteArray[INDEX_PACKET_LENGTH] = getMsgLength()
        if(this is IFunction){
            byteArray[INDEX_COMMAND] = getCommandByte()
        }
        createDataBytes(byteArray)
        if (hasCrcData()) {
            byteArray[byteArray.size - 1] = calculateCRC(byteArray)
        }
        return byteArray
    }

    protected abstract fun createDataBytes(byteArray: ByteArray)

    protected fun calculateCRC(bytes: ByteArray): Byte {
        val sum = bytes.sumOf { it.toUByte().toInt() } // 计算总和，确保无符号加法
        return (sum and 0xFF).toByte() // 只保留低 8 位
    }

    open fun hasCrcData(): Boolean {
        return true
    }
}