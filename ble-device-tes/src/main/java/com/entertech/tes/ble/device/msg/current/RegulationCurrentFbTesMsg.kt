package com.entertech.tes.ble.device.msg.current

import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

class RegulationCurrentFbTesMsg : BaseReceiveTesMsg(), IRegulationCurrentFunction {

    companion object {
        /**
         * 0xFF:电流增加 0XEE:电流减小
         * */
        const val INDEX_REGULATION_TYPE = 1

        /**
         * 0x00:成功 0X01:剩余时间不足，无法调至到此模式；0x02:设备正在缓降阶段无法 调节电流;0x03:失败
         * */
        const val INDEX_REGULATION_RESULT = 2
    }

    override fun processMsgData(byteArray: ByteArray): Boolean {
        val regulationType = byteArray.getOrNull(INDEX_DATA_START + INDEX_REGULATION_TYPE)
        val regulationResult = byteArray.getOrNull(INDEX_DATA_START + INDEX_REGULATION_RESULT)
        when (regulationResult) {

        }
        return true
    }

    override fun getMsgLength(): Byte {
        return 0x09
    }

}