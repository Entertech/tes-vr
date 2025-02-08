package com.entertech.tes.vr.mode.stimulate

import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PseudoStimulateModeActivity : BaseTesActivity<PseudoStimulateModeViewModel>() {
    private var tvDeviceInfo: TextView? = null
    private var tvStartPseudoStimulate: TextView? = null
    private var tvEndPseudoStimulate: TextView? = null
    private var tvReceiveMsg: TextView? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.mode_pseudo_stimulate_activity
    }

    override fun initActivityView() {
        super.initActivityView()
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvStartPseudoStimulate = findViewById(R.id.tvStartPseudoStimulate)
        tvEndPseudoStimulate = findViewById(R.id.tvEndPseudoStimulate)
        tvStartPseudoStimulate?.setOnClickListener(this)
        tvEndPseudoStimulate?.setOnClickListener(this)
    }

    override fun initActivityData() {
        super.initActivityData()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.deviceInfo.collect {
                if (it.isNotEmpty()) {
                    tvDeviceInfo?.text = it
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.receiveMsg.collect {
                if (it.isNotEmpty()) {
                    tvReceiveMsg?.text = it
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvStartPseudoStimulate -> {
                viewModel.sendMessage{it?.startFakeMode()}
            }

            R.id.tvEndPseudoStimulate -> {
                viewModel.sendMessage{it?.stopFakeMode()}
            }
        }
    }
}