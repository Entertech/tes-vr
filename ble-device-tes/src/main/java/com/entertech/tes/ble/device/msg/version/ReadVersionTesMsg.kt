package com.entertech.tes.ble.device.msg.version

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

class ReadVersionTesMsg : BaseSendTesMsg(), IVersionFunction {

    override fun getMsgLength(): Byte {
        return 0x06
    }

    override fun createDataBytes(byteArray: ByteArray) {

    }
}