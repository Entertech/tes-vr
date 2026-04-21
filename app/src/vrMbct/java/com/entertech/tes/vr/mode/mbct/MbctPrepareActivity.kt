package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MbctPrepareActivity : BaseTesActivity<MbctPrepareViewModel>() {

    private var tvProcessHint: TextView? = null
    private var tvGuideStatus: TextView? = null
    private var tvCountdown: TextView? = null
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null

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
            viewModel.navigationEvent.collect { sessionId ->
                val intent = Intent(this@MbctPrepareActivity, MbctCourseListActivity::class.java)
                intent.putExtra(MbctPrepareViewModel.EXTRA_SESSION_ID, sessionId)
                startActivity(intent)
                finish()
            }
        }
    }
}
