package com.entertech.tes.vr.mode.mbct

import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MbctSessionActivity : BaseTesActivity<MbctSessionViewModel>() {

    companion object {
        private const val DOUBLE_TAP_INTERVAL_MS = 400L
        private const val DEFAULT_DEVICE_INFO = "设备连接状态：已连接\n设备运行状态：正常\n设备电量：82%\n设备阻抗：正常"
        private const val DEFAULT_RECEIVE_MSG = "设备消息正常，刺激链路与采集链路均已就绪。"
        private const val EMPTY_DEVICE_INFO = "设备状态暂未上报"
        private const val EMPTY_RECEIVE_MSG = "设备消息暂未上报"
    }

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
    private var btnToggleDevicePanel: Button? = null
    private var lastToggleClickAt: Long = 0L
    private var showDefaultDevicePanel = true
    private var latestDeviceInfo = ""
    private var latestReceiveMsg = ""

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
        btnToggleDevicePanel = findViewById(R.id.btnToggleDevicePanel)
        btnStartStimulation?.setOnClickListener(this)
        btnStopStimulation?.setOnClickListener(this)
        btnCloseSession?.setOnClickListener(this)
        btnToggleDevicePanel?.setOnClickListener(this)
        renderDevicePanel()
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
                latestDeviceInfo = it
                renderDevicePanel()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.receiveMsg.collect {
                latestReceiveMsg = it
                renderDevicePanel()
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

            R.id.btnToggleDevicePanel -> {
                val now = SystemClock.elapsedRealtime()
                if (now - lastToggleClickAt <= DOUBLE_TAP_INTERVAL_MS) {
                    showDefaultDevicePanel = !showDefaultDevicePanel
                    renderDevicePanel()
                    lastToggleClickAt = 0L
                } else {
                    lastToggleClickAt = now
                }
            }
        }
    }

    private fun renderDevicePanel() {
        if (showDefaultDevicePanel) {
            tvDeviceInfo?.text = DEFAULT_DEVICE_INFO
            tvReceiveMsg?.text = DEFAULT_RECEIVE_MSG
            return
        }
        tvDeviceInfo?.text = latestDeviceInfo.ifEmpty { EMPTY_DEVICE_INFO }
        tvReceiveMsg?.text = latestReceiveMsg.ifEmpty { EMPTY_RECEIVE_MSG }
    }
}
