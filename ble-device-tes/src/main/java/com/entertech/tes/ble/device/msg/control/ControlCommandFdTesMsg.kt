package com.entertech.tes.ble.device.msg.control

import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

class ControlCommandFdTesMsg : BaseReceiveTesMsg(), IControlCommandFunction {

    companion object{
        /**
         * DATA[0]：设备启动反馈：0x01:启动成功 0x02:启动失败
         * DATA[0]：设备断开连接反馈：0x01:断开连接成功 0x02:断开连接失败
         * */
        const val INDEX_CONTROL_RESULT = 0
    }
    override fun processMsgData(byteArray: ByteArray): Boolean {

        return true
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

}