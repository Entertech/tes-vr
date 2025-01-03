package com.entertech.tes.ble.device.msg.rename

import com.entertech.tes.ble.device.msg.IFunction

interface IRenameFunction: IFunction {
    companion object {
        const val COMMAND_BYTE_RENAME: Byte = 0x03.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_RENAME
    }
}