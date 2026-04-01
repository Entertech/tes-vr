package com.entertech.tes.vr.mode.biomarker

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BiomarkerCompareModeActivity : BaseTesActivity<BiomarkerCompareModeViewModel>() {

    private var tvProcessHint: TextView? = null
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null
    private var tvBaselineStatus: TextView? = null
    private var tvStimulationStatus: TextView? = null
    private var tvPostStatus: TextView? = null
    private var tvCompareStatus: TextView? = null
    private var btnCollectBaselineEeg: Button? = null
    private var btnStartStimulation: Button? = null
    private var btnCollectPostEeg: Button? = null
    private var btnCompareData: Button? = null
    private var btnOpenInsightReport: Button? = null
    private var btnRunFullProcess: Button? = null
    private var btnResetProcessState: Button? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.mode_biomarker_compare_activity
    }

    override fun initActivityView() {
        super.initActivityView()
        tvProcessHint = findViewById(R.id.tvProcessHint)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvBaselineStatus = findViewById(R.id.tvBaselineStatus)
        tvStimulationStatus = findViewById(R.id.tvStimulationStatus)
        tvPostStatus = findViewById(R.id.tvPostStatus)
        tvCompareStatus = findViewById(R.id.tvCompareStatus)
        btnCollectBaselineEeg = findViewById(R.id.btnCollectBaselineEeg)
        btnStartStimulation = findViewById(R.id.btnStartStimulation)
        btnCollectPostEeg = findViewById(R.id.btnCollectPostEeg)
        btnCompareData = findViewById(R.id.btnCompareData)
        btnOpenInsightReport = findViewById(R.id.btnOpenInsightReport)
        btnRunFullProcess = findViewById(R.id.btnRunFullProcess)
        btnResetProcessState = findViewById(R.id.btnResetProcessState)

        btnCollectBaselineEeg?.setOnClickListener(this)
        btnStartStimulation?.setOnClickListener(this)
        btnCollectPostEeg?.setOnClickListener(this)
        btnCompareData?.setOnClickListener(this)
        btnOpenInsightReport?.setOnClickListener(this)
        btnRunFullProcess?.setOnClickListener(this)
        btnResetProcessState?.setOnClickListener(this)
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

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect { state ->
                tvProcessHint?.text = state.processHint
                tvBaselineStatus?.text = state.baselineStatus
                tvStimulationStatus?.text = state.stimulationStatus
                tvPostStatus?.text = state.postStatus
                tvCompareStatus?.text = state.compareStatus
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCollectBaselineEeg -> {
                viewModel.requestBaselineCollection()
            }

            R.id.btnStartStimulation -> {
                viewModel.requestStimulation()
            }

            R.id.btnCollectPostEeg -> {
                viewModel.requestPostCollection()
            }

            R.id.btnCompareData -> {
                viewModel.requestCompare()
            }

            R.id.btnOpenInsightReport -> {
                startActivity(Intent(this, BiomarkerInsightReportActivity::class.java))
            }

            R.id.btnRunFullProcess -> {
                viewModel.requestOneClickProcess()
            }

            R.id.btnResetProcessState -> {
                viewModel.resetProcessState()
            }
        }
    }
}
