package com.entertech.tes.vr.mode

import android.content.Intent
import android.widget.Toast
import com.entertech.tes.vr.BaseTesViewModel
import com.entertech.tes.vr.mode.normal.NormalModeActivity
import com.entertech.tes.vr.mode.stimulate.PseudoStimulateModeActivity

class ChooseModeViewModel : BaseTesViewModel() {

    companion object {
        private const val BIOMARKER_COMPARE_MODE_ACTIVITY =
            "com.entertech.tes.vr.mode.biomarker.BiomarkerCompareModeActivity"
        private const val CHANGE_CURRENT_MODE_ACTIVITY =
            "com.entertech.tes.vr.mode.change.ChangeModeActivity"
    }

    fun startNormalMode(activity: ChooseModeActivity) {
        activity.startActivity(Intent(activity, NormalModeActivity::class.java))
    }

    fun startPseudoStimulateMode(activity: ChooseModeActivity) {
        activity.startActivity(Intent(activity, PseudoStimulateModeActivity::class.java))
    }

    fun startChangeCurrentMode(activity: ChooseModeActivity) {
        val intent = Intent().setClassName(activity, CHANGE_CURRENT_MODE_ACTIVITY)
        if (intent.resolveActivity(activity.packageManager) == null) {
            Toast.makeText(activity, "当前变种未启用动态改变电流模式", Toast.LENGTH_SHORT).show()
            return
        }
        activity.startActivity(intent)
    }

    fun startBiomarkerCompareMode(activity: ChooseModeActivity) {
        val intent = Intent().setClassName(activity, BIOMARKER_COMPARE_MODE_ACTIVITY)
        if (intent.resolveActivity(activity.packageManager) == null) {
            Toast.makeText(activity, "当前变种未启用脑波前后测对比模式", Toast.LENGTH_SHORT).show()
            return
        }
        activity.startActivity(intent)
    }
}
