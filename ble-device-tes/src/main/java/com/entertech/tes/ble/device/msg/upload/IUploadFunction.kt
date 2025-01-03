package com.entertech.tes.ble.device.msg.upload

import com.entertech.tes.ble.device.msg.IFunction

interface IUploadFunction: IFunction {
    companion object {
        const val COMMAND_BYTE_UPLOAD: Byte = 0x05.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_UPLOAD
    }
}