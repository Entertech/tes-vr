package com.entertech.tes.vr.mode.mbct

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MbctDeviceInfoActivity : BaseTesActivity<MbctDeviceInfoViewModel>() {

    private var tvHint: TextView? = null
    private var tvConnectStatus: TextView? = null
    private var tvDeviceName: TextView? = null
    private var tvDeviceMac: TextView? = null
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null
    private var btnRefreshDeviceInfo: Button? = null
    private var btnBack: Button? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.activity_mbct_device_info
    }

    override fun initActivityView() {
        super.initActivityView()
        tvHint = findViewById(R.id.tvHint)
        tvConnectStatus = findViewById(R.id.tvConnectStatus)
        tvDeviceName = findViewById(R.id.tvDeviceName)
        tvDeviceMac = findViewById(R.id.tvDeviceMac)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        btnRefreshDeviceInfo = findViewById(R.id.btnRefreshDeviceInfo)
        btnBack = findViewById(R.id.btnBack)
        btnRefreshDeviceInfo?.setOnClickListener(this)
        btnBack?.setOnClickListener(this)
    }

    override fun initActivityData() {
        super.initActivityData()
        viewModel.refreshDeviceInfo()

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect { state ->
                tvHint?.text = state.hintText
                tvConnectStatus?.text = state.connectStatusText
                tvDeviceName?.text = "设备名称：${state.deviceName}"
                tvDeviceMac?.text = "MAC 地址：${state.deviceMac}"
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.deviceInfo.collect {
                tvDeviceInfo?.text = it.ifEmpty { "设备运行信息暂未上报" }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.receiveMsg.collect {
                tvReceiveMsg?.text = it.ifEmpty { "设备消息暂未上报" }
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btnRefreshDeviceInfo -> {
                viewModel.refreshDeviceInfo()
            }

            R.id.btnBack -> {
                finish()
            }
        }
    }
}
