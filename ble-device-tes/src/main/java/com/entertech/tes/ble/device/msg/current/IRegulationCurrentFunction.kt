package com.entertech.tes.ble.device.msg.current

import com.entertech.tes.ble.device.msg.IFunction

interface IRegulationCurrentFunction: IFunction {
    companion object {
        const val COMMAND_BYTE_REGULATION_CURRENT: Byte = 0x04.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_REGULATION_CURRENT
    }
}