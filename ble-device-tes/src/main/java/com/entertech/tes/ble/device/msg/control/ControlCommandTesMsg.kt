package com.entertech.tes.ble.device.msg.control

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

class ControlCommandTesMsg(private val controlCommand:Byte) : BaseSendTesMsg(),IControlCommandFunction {

    companion object {
        const val INDEX_CONTROL_COMMAND = 0

        const val CONTROL_COMMAND_START: Byte = 0x01
        const val CONTROL_COMMAND_STOP: Byte = 0x02
        const val CONTROL_COMMAND_DISCONNECT: Byte = 0x03
        const val CONTROL_COMMAND_POWER_OFF: Byte = 0x04
    }

    override fun createDataBytes(byteArray: ByteArray) {
        byteArray[INDEX_DATA_START + INDEX_CONTROL_COMMAND] = controlCommand
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

}