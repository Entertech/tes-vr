package com.entertech.tes.ble.device.msg

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.INDEX_COMMAND
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.INDEX_INDEX
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.INDEX_PACKET_HEAD_1
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.INDEX_PACKET_HEAD_2
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.INDEX_PACKET_LENGTH
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.PACKET_HEAD_1
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.PACKET_HEAD_2
import com.entertech.tes.ble.device.msg.BaseTesMsg.Companion.PACKET_INDEX
import com.entertech.tes.ble.device.msg.control.ControlCommandFdTesMsg
import com.entertech.tes.ble.device.msg.control.IControlCommandFunction
import com.entertech.tes.ble.device.msg.current.IRegulationCurrentFunction
import com.entertech.tes.ble.device.msg.current.RegulationCurrentFbTesMsg
import com.entertech.tes.ble.device.msg.rename.IRenameFunction
import com.entertech.tes.ble.device.msg.rename.RenameFbTesMsg
import com.entertech.tes.ble.device.msg.set.ISetFunction
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg
import com.entertech.tes.ble.device.msg.shakehand.IShakeHandFunction
import com.entertech.tes.ble.device.msg.shakehand.ShakeHandsFbTesMsg
import com.entertech.tes.ble.device.msg.version.IVersionFunction
import com.entertech.tes.ble.device.msg.version.ReadVersionFbTesMsg

object AnalysisTesMsgTool {
    private const val TAG = "AnalysisTesMsgTool"

    fun processMsg(byteArray: ByteArray): BaseReceiveTesMsg? {
        val size = byteArray.size
        val packetHead1 = byteArray.getOrNull(INDEX_PACKET_HEAD_1)
        if (packetHead1 != PACKET_HEAD_1) {
            TesVrLog.e(TAG, "包头1 不是 55 $packetHead1")
            return null
        }
        val packetHead2 = byteArray.getOrNull(INDEX_PACKET_HEAD_2)
        if (packetHead2 != PACKET_HEAD_2) {
            TesVrLog.e(TAG, "包头2 不是 AA $packetHead2")
            return null
        }
        val index = byteArray.getOrNull(INDEX_INDEX)
        if (index != PACKET_INDEX) {
            TesVrLog.e(TAG, "index位 不是 01 $index")
            return null
        }
        val length = byteArray.getOrNull(INDEX_PACKET_LENGTH)?.toInt()
        if (length != size) {
            TesVrLog.e(TAG, "length $length  不等于 size  $size")
            return null
        }
        //命令符
        val cmd = byteArray.getOrNull(INDEX_COMMAND)
        val receiveTesMsg: BaseReceiveTesMsg? = when (cmd) {
            IShakeHandFunction.COMMAND_BYTE_SHAKE_HAND -> {
                ShakeHandsFbTesMsg()
            }

            IControlCommandFunction.COMMAND_BYTE_CONTROL_COMMAND -> {
                ControlCommandFdTesMsg()
            }

            IRegulationCurrentFunction.COMMAND_BYTE_REGULATION_CURRENT -> {
                RegulationCurrentFbTesMsg()
            }

            IRenameFunction.COMMAND_BYTE_RENAME -> {
                RenameFbTesMsg()
            }

            ISetFunction.COMMAND_BYTE_SET -> {
                SettingArgFbTesMsg()
            }

            IVersionFunction.COMMAND_BYTE_VERSION -> {
                ReadVersionFbTesMsg()
            }

            else -> {
                TesVrLog.e(TAG, "未知命令 $cmd")
                null
            }
        }
        receiveTesMsg?.processMsgData(byteArray)
        return receiveTesMsg

    }
}