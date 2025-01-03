package com.entertech.tes.ble.device.msg.version

import com.entertech.tes.ble.device.msg.IFunction

interface IVersionFunction:IFunction {
    companion object {
        const val COMMAND_BYTE_VERSION: Byte = 0x08.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_VERSION
    }
}