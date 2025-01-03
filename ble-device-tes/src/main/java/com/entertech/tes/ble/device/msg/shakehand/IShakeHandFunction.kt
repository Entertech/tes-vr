package com.entertech.tes.ble.device.msg.shakehand

import com.entertech.tes.ble.device.msg.IFunction

interface IShakeHandFunction:IFunction {
    companion object {
        const val COMMAND_BYTE_SHAKE_HAND: Byte = 0x00.toByte()
    }

    override fun getCommandByte(): Byte {
        return COMMAND_BYTE_SHAKE_HAND
    }


}