package com.entertech.tes.ble.device.msg.send

import com.entertech.tes.ble.device.msg.BaseTesMsg

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
        byteArray[INDEX_COMMAND] = getCommandByte()
        createDataBytes(byteArray)
        return byteArray
    }

    protected abstract fun createDataBytes(byteArray: ByteArray)
}