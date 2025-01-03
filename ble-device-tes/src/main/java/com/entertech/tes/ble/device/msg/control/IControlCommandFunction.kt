package com.entertech.tes.ble.device.msg.control

import com.entertech.tes.ble.device.msg.IFunction

interface IControlCommandFunction:IFunction {
    companion object {
        const val COMMAND_BYTE_CONTROL_COMMAND: Byte = 0x01.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_CONTROL_COMMAND
    }
}