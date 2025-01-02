package com.entertech.tes.ble.device.msg.receive

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.BaseTesMsg

/**
 * app 接收的消息
 * */
abstract class BaseReceiveTesMsg : BaseTesMsg() {
    companion object {
        private const val TAG = "BaseReceiveTesMsg"


    }


    abstract fun processMsgData(byteArray: ByteArray): Boolean
}