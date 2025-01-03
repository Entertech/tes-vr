package com.entertech.tes.ble.device.msg.rename

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

class RenameTesMsg(private val nameBytes:ByteArray) : BaseSendTesMsg(), IRenameFunction {
    override fun createDataBytes(byteArray: ByteArray) {
        nameBytes.forEachIndexed { index, byte ->
            byteArray[INDEX_DATA_START + index] = byte
        }
    }

    override fun getMsgLength(): Byte {
        return 0x0D
    }

}