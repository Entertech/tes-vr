package com.entertech.tes.ble.device.msg.version

import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

class ReadVersionFbTesMsg : BaseReceiveTesMsg(), IVersionFunction {

    /**
     * 使用正则表达式匹配 V-数字
     * */
    private val regex by lazy {
        Regex("V(\\d+)")
    }

    /**
     * 硬件版本号
     * */
    var hardwareVersion: String = ""

    /**
     * 软件版本号
     * */
    var softwareVersion: String = ""

    /**
     * 协议版本号
     * */
    var protocolVersion: String = ""

    override fun getMsgLength(): Byte {
        return 0x14
    }

    override fun processMsgData(byteArray: ByteArray): Boolean {
        // 提取第六个字节之后的数据
        val relevantBytes = byteArray.sliceArray(INDEX_DATA_START until byteArray.size)
        // 转换为字符串
        val stringData = relevantBytes.toString(Charsets.UTF_8)
        //：硬件版本号-软件版本号-协议版本号
        regex.findAll(stringData).map { it.groupValues[1] }.toList().forEach {
            when {
                hardwareVersion.isEmpty() -> hardwareVersion = it
                softwareVersion.isEmpty() -> softwareVersion = it
                protocolVersion.isEmpty() -> protocolVersion = it
            }
        }

        return true
    }

}