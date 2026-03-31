package com.entertech.tes.vr.mode.change

import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeModeActivity : BaseTesActivity<ChangeModeViewModel>() {
    private var tvDeviceInfo: TextView? = null
    private var tvStartChangeMode: TextView? = null
    private var tvStopChangeMode: TextView? = null
    private var tvReceiveMsg: TextView? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.mode_pseudo_stimulate_activity
    }

    override fun initActivityView() {
        super.initActivityView()
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvStartChangeMode = findViewById(R.id.tvStartPseudoStimulate)
        tvStopChangeMode = findViewById(R.id.tvEndPseudoStimulate)
        tvStartChangeMode?.text = "开始动态电流"
        tvStopChangeMode?.text = "停止动态电流"
        tvStartChangeMode?.setOnClickListener(this)
        tvStopChangeMode?.setOnClickListener(this)
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
                viewModel.startChangeMode()
            }

            R.id.tvEndPseudoStimulate -> {
                viewModel.stopChangeMode()
            }
        }
    }
}
