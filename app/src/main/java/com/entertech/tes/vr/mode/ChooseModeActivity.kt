package com.entertech.tes.vr.mode

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R

class ChooseModeActivity : BaseTesActivity<ChooseModeViewModel>() {
    private var tvNormalMode: TextView? = null
    private var tvPseudoStimulateMode: TextView? = null
    private var tvBiomarkerCompareMode: TextView? = null
    private var tvChangeCurrentMode: TextView? = null
    private var tvVrMbctMode: TextView? = null
    private var btnOpenVrMbctDeviceInfo: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_mode_activity)
        tvNormalMode = findViewById(R.id.tvNormalMode)
        tvPseudoStimulateMode = findViewById(R.id.tvPseudoStimulateMode)
        tvChangeCurrentMode = findViewById(R.id.tvChangeCurrentMode)
        tvBiomarkerCompareMode = findViewById(R.id.tvBiomarkerCompareMode)
        tvVrMbctMode = findViewById(R.id.tvVrMbctMode)
        btnOpenVrMbctDeviceInfo = findViewById(R.id.btnOpenVrMbctDeviceInfo)
        tvNormalMode?.setOnClickListener(this)
        tvChangeCurrentMode?.setOnClickListener(this)
        tvPseudoStimulateMode?.setOnClickListener(this)
        tvBiomarkerCompareMode?.setOnClickListener(this)
        tvVrMbctMode?.setOnClickListener(this)
        btnOpenVrMbctDeviceInfo?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v){
            tvNormalMode -> {
                viewModel.startNormalMode(this)
            }
            tvPseudoStimulateMode -> {
                viewModel.startPseudoStimulateMode(this)
            }
            tvBiomarkerCompareMode -> {
                viewModel.startBiomarkerCompareMode(this)
            }

            tvChangeCurrentMode -> {
                viewModel.startChangeCurrentMode(this)
            }

            tvVrMbctMode -> {
                viewModel.startVrMbctMode(this)
            }

            btnOpenVrMbctDeviceInfo -> {
                viewModel.startVrMbctDeviceInfo(this)
            }
        }
    }
}
