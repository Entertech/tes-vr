package com.entertech.tes.vr.mode

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R

class ChooseModeActivity : BaseTesActivity<ChooseModeViewModel>() {
    private var tvNormalMode:TextView?=null
    private var tvPseudoStimulateMode:TextView?=null
    private var tvBiomarkerCompareMode: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_mode_activity)
        tvNormalMode=findViewById(R.id.tvNormalMode)
        tvPseudoStimulateMode=findViewById(R.id.tvPseudoStimulateMode)
        tvBiomarkerCompareMode = findViewById(R.id.tvBiomarkerCompareMode)
        tvNormalMode?.setOnClickListener(this)
        tvPseudoStimulateMode?.setOnClickListener(this)
        tvBiomarkerCompareMode?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v?.id){
            R.id.tvNormalMode->{
                viewModel.startNormalMode(this)
            }
            R.id.tvPseudoStimulateMode->{
                viewModel.startPseudoStimulateMode(this)
            }
            R.id.tvBiomarkerCompareMode -> {
                viewModel.startBiomarkerCompareMode(this)
            }
        }
    }
}
