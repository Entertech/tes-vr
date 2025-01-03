package com.entertech.tes.ble.device.msg

/**
 * app 接收的消息
 * */
abstract class BaseReceiveTesMsg : BaseTesMsg() {
    companion object {
        private const val TAG = "BaseReceiveTesMsg"


    }

    abstract fun processMsgData(byteArray: ByteArray): Boolean
}