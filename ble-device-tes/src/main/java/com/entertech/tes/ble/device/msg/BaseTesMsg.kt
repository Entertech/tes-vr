package com.entertech.tes.ble.device.msg

abstract class BaseTesMsg {
    companion object {
        const val PACKET_HEAD_1 = 0x55.toByte()
        const val PACKET_HEAD_2 = 0xAA.toByte()
        const val PACKET_INDEX = 0x01.toByte()

        const val INDEX_PACKET_HEAD_1 = 0
        const val INDEX_PACKET_HEAD_2 = 1
        const val INDEX_INDEX = 2

        /**
         * 包长度
         * */
        const val INDEX_PACKET_LENGTH = 3

        /**
         * 命令符
         * */
        const val INDEX_COMMAND = 4

        const val INDEX_DATA_START = 5
    }


    abstract fun getMsgLength(): Byte

    abstract fun getCommandByte(): Byte


}