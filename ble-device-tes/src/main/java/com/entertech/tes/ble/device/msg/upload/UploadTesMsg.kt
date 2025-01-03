package com.entertech.tes.ble.device.msg.upload

import com.entertech.tes.ble.device.msg.BaseSendTesMsg

class UploadTesMsg : BaseSendTesMsg(), IUploadFunction {

    companion object {
        const val index_setting_mode = 0

        /**
         * 当前设定的电流
         * */
        const val index_setting_current = 1

        /**
         * ：当前设定的时间
         * */
        const val index_setting_time = 2

        /**
         * ：当前设定的频率
         * */
        const val INDEX_SETTING_FREQUENCY = 3

        /**
         * 刺激剩余时间
         * */
        const val INDEX_Stimulate_remain_time = 5

        /**
         * 当前阻抗值
         * */
        const val INDEX_impedance_value = 7

        /**
         * 当前电流值
         * */
        const val INDEX_current_value = 9

        /**
         * 设备电量
         * */
        const val INDEX_device_battery = 10

        /**
         * 运行状态 0x00：无效 0x01：设备运行中 0x02: 设备就绪中 0x03：设备故障
         * */
        const val INDEX_device_status = 11

        /**
         * 停止标记 0x00：正常周期性发送 0x01:正在停止中 （正常结束缓降） 0x02:停止完成；0x03：停止失败 0x04: 停止命令已经接收到，主动缓降停止；
         * */
        const val INDEX_stop_flag = 12

        /**
         * 设定电流的阶段
         * */
        const val INDEX_current_level = 13

        /**
         * 电流限制是否放开 0：放开限制 1：限制
         * */
        const val INDEX_current_limit = 14

    }

    override fun createDataBytes(byteArray: ByteArray) {

    }

    override fun getMsgLength(): Byte {
        return 0x15
    }


    override fun hasCrcData(): Boolean {
        return false
    }
}