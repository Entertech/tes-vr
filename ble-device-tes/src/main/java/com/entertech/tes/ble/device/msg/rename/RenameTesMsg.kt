package com.entertech.tes.ble.device.msg.rename

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

class RenameTesMsg : BaseSendTesMsg(), IRenameFunction {
    override fun createDataBytes(byteArray: ByteArray) {

    }

    override fun getMsgLength(): Byte {
        return 0x0D
    }

}