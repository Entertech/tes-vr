package com.entertech.tes.vr.mode.stimulate

import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg
import com.entertech.tes.ble.device.msg.stimulate.pseudo.start.StartPseudoStimulateFbTesMsg
import com.entertech.tes.ble.device.msg.stimulate.pseudo.stop.StopPseudoStimulateFbTesMsg
import com.entertech.tes.vr.BaseTesViewModel

class PseudoStimulateModeViewModel : BaseTesViewModel() {
    companion object {
        private const val TAG = "PseudoStimulateModeViewModel"
    }

    override fun processMsg(msg: BaseReceiveTesMsg?) {
        super.processMsg(msg)
        when (msg) {
            is StartPseudoStimulateFbTesMsg -> {
                if (msg.startSuccess) {
                    TesVrLog.d(TAG, "开始伪刺激指令成功")
                } else {
                    TesVrLog.d(TAG, "开始伪刺激指令失败")
                }
            }

            is StopPseudoStimulateFbTesMsg -> {
                if (msg.stopSuccess) {
                    TesVrLog.d(TAG, "停止伪刺激指令成功")
                } else {
                    TesVrLog.d(TAG, "停止伪刺激指令失败")
                }
            }
        }
    }
}