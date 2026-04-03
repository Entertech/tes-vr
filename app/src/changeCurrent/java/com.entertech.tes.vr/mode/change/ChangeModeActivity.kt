package com.entertech.tes.vr.mode.change

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeModeActivity : BaseTesActivity<ChangeModeViewModel>() {
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null
    private var tvProcessHint: TextView? = null
    private var tvReadinessStatus: TextView? = null
    private var tvStrategyStatus: TextView? = null
    private var tvStimulationStatus: TextView? = null
    private var tvSafetyStatus: TextView? = null
    private var btnMarkDeviceReady: Button? = null
    private var btnSimulateStrategyRefresh: Button? = null
    private var btnStartChangeMode: Button? = null
    private var btnStopChangeMode: Button? = null
    private var btnOpenPrecheckBoard: Button? = null
    private var btnOpenInsightReport: Button? = null
    private var btnOpenTrendOverview: Button? = null
    private var btnResetProcessState: Button? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.mode_auto_change_current_activity
    }

    override fun initActivityView() {
        super.initActivityView()
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvProcessHint = findViewById(R.id.tvProcessHint)
        tvReadinessStatus = findViewById(R.id.tvReadinessStatus)
        tvStrategyStatus = findViewById(R.id.tvStrategyStatus)
        tvStimulationStatus = findViewById(R.id.tvStimulationStatus)
        tvSafetyStatus = findViewById(R.id.tvSafetyStatus)
        btnMarkDeviceReady = findViewById(R.id.btnMarkDeviceReady)
        btnSimulateStrategyRefresh = findViewById(R.id.btnSimulateStrategyRefresh)
        btnStartChangeMode = findViewById(R.id.btnStartChangeMode)
        btnStopChangeMode = findViewById(R.id.btnStopChangeMode)
        btnOpenPrecheckBoard = findViewById(R.id.btnOpenPrecheckBoard)
        btnOpenInsightReport = findViewById(R.id.btnOpenInsightReport)
        btnOpenTrendOverview = findViewById(R.id.btnOpenTrendOverview)
        btnResetProcessState = findViewById(R.id.btnResetProcessState)

        btnMarkDeviceReady?.setOnClickListener(this)
        btnSimulateStrategyRefresh?.setOnClickListener(this)
        btnStartChangeMode?.setOnClickListener(this)
        btnStopChangeMode?.setOnClickListener(this)
        btnOpenPrecheckBoard?.setOnClickListener(this)
        btnOpenInsightReport?.setOnClickListener(this)
        btnOpenTrendOverview?.setOnClickListener(this)
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
                tvReadinessStatus?.text = state.readinessStatus
                tvStrategyStatus?.text = state.strategyStatus
                tvStimulationStatus?.text = state.stimulationStatus
                tvSafetyStatus?.text = state.safetyStatus
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMarkDeviceReady -> {
                viewModel.markDeviceReady()
            }

            R.id.btnSimulateStrategyRefresh -> {
                viewModel.simulateStrategyRefresh()
            }

            R.id.btnStartChangeMode -> {
                viewModel.startChangeMode()
            }

            R.id.btnStopChangeMode -> {
                viewModel.stopChangeMode()
            }

            R.id.btnOpenPrecheckBoard -> {
                startActivity(Intent(this, ChangeCurrentPrecheckBoardActivity::class.java))
            }

            R.id.btnOpenInsightReport -> {
                startActivity(Intent(this, ChangeCurrentInsightReportActivity::class.java))
            }

            R.id.btnOpenTrendOverview -> {
                startActivity(Intent(this, ChangeCurrentTrendOverviewActivity::class.java))
            }

            R.id.btnResetProcessState -> {
                viewModel.resetProcessState()
            }
        }
    }
}
