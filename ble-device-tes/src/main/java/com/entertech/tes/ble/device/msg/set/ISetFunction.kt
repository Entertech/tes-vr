package com.entertech.tes.ble.device.msg.set

import com.entertech.tes.ble.device.msg.IFunction

interface ISetFunction:IFunction {
    companion object {
        const val COMMAND_BYTE_SET: Byte = 0x02.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_SET
    }
}