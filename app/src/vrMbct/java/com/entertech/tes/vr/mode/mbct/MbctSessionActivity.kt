package com.entertech.tes.vr.mode.mbct

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MbctSessionActivity : BaseTesActivity<MbctSessionViewModel>() {

    private var tvProcessHint: TextView? = null
    private var tvSelectedCourse: TextView? = null
    private var tvStimulationStatus: TextView? = null
    private var tvPostGuideStatus: TextView? = null
    private var tvCountdown: TextView? = null
    private var tvRecordStatus: TextView? = null
    private var tvSessionStatus: TextView? = null
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null
    private var btnStartStimulation: Button? = null
    private var btnStopStimulation: Button? = null
    private var btnCloseSession: Button? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.activity_mbct_session
    }

    override fun initActivityView() {
        super.initActivityView()
        tvProcessHint = findViewById(R.id.tvProcessHint)
        tvSelectedCourse = findViewById(R.id.tvSelectedCourse)
        tvStimulationStatus = findViewById(R.id.tvStimulationStatus)
        tvPostGuideStatus = findViewById(R.id.tvPostGuideStatus)
        tvCountdown = findViewById(R.id.tvCountdown)
        tvRecordStatus = findViewById(R.id.tvRecordStatus)
        tvSessionStatus = findViewById(R.id.tvSessionStatus)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        btnStartStimulation = findViewById(R.id.btnStartStimulation)
        btnStopStimulation = findViewById(R.id.btnStopStimulation)
        btnCloseSession = findViewById(R.id.btnCloseSession)
        btnStartStimulation?.setOnClickListener(this)
        btnStopStimulation?.setOnClickListener(this)
        btnCloseSession?.setOnClickListener(this)
    }

    override fun initActivityData() {
        super.initActivityData()
        val sessionId = intent.getStringExtra(MbctPrepareViewModel.EXTRA_SESSION_ID).orEmpty()
        val courseId = intent.getStringExtra(MbctCourseListActivity.EXTRA_COURSE_ID).orEmpty()
        viewModel.bindSession(sessionId, courseId)

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect { state ->
                tvProcessHint?.text = state.processHint
                tvSelectedCourse?.text = state.selectedCourse
                tvStimulationStatus?.text = state.stimulationStatus
                tvPostGuideStatus?.text = state.postGuideStatus
                tvCountdown?.text = state.countdownText
                tvRecordStatus?.text = state.recordStatus
                tvSessionStatus?.text = state.sessionStatus
            }
        }

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
            R.id.btnStartStimulation -> {
                viewModel.startStimulation()
            }

            R.id.btnStopStimulation -> {
                viewModel.stopStimulation()
            }

            R.id.btnCloseSession -> {
                finish()
            }
        }
    }
}
