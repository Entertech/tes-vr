package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MbctPrepareActivity : BaseTesActivity<MbctPrepareViewModel>() {

    companion object {
        private const val DOUBLE_TAP_INTERVAL_MS = 400L
        private const val DEFAULT_DEVICE_INFO = "设备连接状态：已连接\n设备运行状态：正常\n设备电量：85%\n设备阻抗：正常"
        private const val DEFAULT_RECEIVE_MSG = "设备消息正常，采集链路已就绪，等待引导阶段完成。"
        private const val EMPTY_DEVICE_INFO = "设备状态暂未上报"
        private const val EMPTY_RECEIVE_MSG = "设备消息暂未上报"
    }

    private var tvProcessHint: TextView? = null
    private var tvGuideStatus: TextView? = null
    private var tvCountdown: TextView? = null
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null
    private var btnToggleDevicePanel: Button? = null
    private var lastToggleClickAt: Long = 0L
    private var showDefaultDevicePanel = true
    private var latestDeviceInfo = ""
    private var latestReceiveMsg = ""

    override fun getActivityLayoutResId(): Int {
        return R.layout.activity_mbct_prepare
    }

    override fun initActivityView() {
        super.initActivityView()
        tvProcessHint = findViewById(R.id.tvProcessHint)
        tvGuideStatus = findViewById(R.id.tvGuideStatus)
        tvCountdown = findViewById(R.id.tvCountdown)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        btnToggleDevicePanel = findViewById(R.id.btnToggleDevicePanel)
        btnToggleDevicePanel?.setOnClickListener(this)
        renderDevicePanel()
    }

    override fun initActivityData() {
        super.initActivityData()
        viewModel.startPrepareGuideIfNeeded()

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect { state ->
                tvProcessHint?.text = state.processHint
                tvGuideStatus?.text = state.guideStatus
                tvCountdown?.text = state.countdownText
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

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.navigationEvent.collect { sessionId ->
                val intent = Intent(this@MbctPrepareActivity, MbctCourseListActivity::class.java)
                intent.putExtra(MbctPrepareViewModel.EXTRA_SESSION_ID, sessionId)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v?.id == R.id.btnToggleDevicePanel) {
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
