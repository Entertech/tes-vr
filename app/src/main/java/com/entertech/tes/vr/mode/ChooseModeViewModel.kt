package com.entertech.tes.vr.mode

import android.content.Intent
import com.entertech.tes.vr.BaseTesViewModel

class ChooseModeViewModel : BaseTesViewModel() {

    companion object {
        private const val BIOMARKER_COMPARE_MODE_ACTIVITY =
            "com.entertech.tes.vr.mode.biomarker.BiomarkerCompareModeActivity"
        private const val CHANGE_CURRENT_MODE_ACTIVITY =
            "com.entertech.tes.vr.mode.change.ChangeModeActivity"
        private const val NORMAL_MODE_ACTIVITY =
            "om.entertech.tes.vr.mode.normal.NormalModeActivity"
        private const val PSEUDO_STIMULATE_MODE_ACTIVITY =
            "com.entertech.tes.vr.mode.stimulate.PseudoStimulateModeActivity"
    }

    fun startNormalMode(activity: ChooseModeActivity) {
        gotoOtherActivityByActivityPath(activity, NORMAL_MODE_ACTIVITY)
    }

    fun startPseudoStimulateMode(activity: ChooseModeActivity) {
        gotoOtherActivityByActivityPath(activity, PSEUDO_STIMULATE_MODE_ACTIVITY)
    }

    fun startChangeCurrentMode(activity: ChooseModeActivity) {
        gotoOtherActivityByActivityPath(activity, CHANGE_CURRENT_MODE_ACTIVITY)
    }

    private fun gotoOtherActivityByActivityPath(
        activity: ChooseModeActivity, activityPath: String
    ) {
        val intent = Intent().setClassName(activity, activityPath)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        }
    }

    fun startBiomarkerCompareMode(activity: ChooseModeActivity) {
        gotoOtherActivityByActivityPath(activity, BIOMARKER_COMPARE_MODE_ACTIVITY)
    }
}
