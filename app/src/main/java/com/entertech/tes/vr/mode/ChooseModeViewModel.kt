package com.entertech.tes.vr.mode

import android.content.Intent
import com.entertech.tes.vr.BaseTesViewModel
import com.entertech.tes.vr.mode.normal.NormalModeActivity
import com.entertech.tes.vr.mode.stimulate.PseudoStimulateModeActivity

class ChooseModeViewModel : BaseTesViewModel() {

    fun startNormalMode(activity: ChooseModeActivity) {
        activity.startActivity(Intent(activity, NormalModeActivity::class.java))
    }

    fun startPseudoStimulateMode(activity: ChooseModeActivity) {
        activity.startActivity(Intent(activity, PseudoStimulateModeActivity::class.java))
    }
}