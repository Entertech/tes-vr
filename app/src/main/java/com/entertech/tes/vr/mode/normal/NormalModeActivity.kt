package com.entertech.tes.vr.mode.normal

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NormalModeActivity : BaseTesActivity<NormalModeViewModel>() {
    private var btnFallCurrent: Button? = null
    private var btnRiseCurrent: Button? = null
    private var btnStartDevice: Button? = null
    private var btnStopDevice: Button? = null
    private var btnTakeOffDevice: Button? = null
    private var btnStopChangeCurrent: Button? = null
    private var etTimeValue: EditText? = null
    private var tvDeviceInfo: TextView? = null
    private var tvReceiveMsg: TextView? = null
    private var etCurrentValue: EditText? = null
    private var switchValueMode: SwitchCompat? = null
    private var switchAutoIncreaseCurrent: SwitchCompat? = null
    private var isHex = false

    override fun getActivityLayoutResId(): Int {
        return R.layout.normal_mode_activity
    }

    override fun initActivityView() {
        super.initActivityView()
        btnFallCurrent = findViewById(R.id.btnFallCurrent)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        btnStopChangeCurrent = findViewById(R.id.btnStopChangeCurrent)
        btnRiseCurrent = findViewById(R.id.btnRiseCurrent)
        btnStopDevice = findViewById(R.id.btnStopDevice)
        btnTakeOffDevice = findViewById(R.id.btnTakeOffDevice)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        switchValueMode = findViewById(R.id.switchValueMode)
        switchAutoIncreaseCurrent = findViewById(R.id.switchAutoIncreaseCurrent)
        btnStartDevice = findViewById(R.id.btnStartDevice)
        btnFallCurrent?.setOnClickListener(this)
        btnRiseCurrent?.setOnClickListener(this)
        btnStartDevice?.setOnClickListener(this)
        btnStopChangeCurrent?.setOnClickListener(this)
        btnStopDevice?.setOnClickListener(this)
        btnTakeOffDevice?.setOnClickListener(this)
        etTimeValue = findViewById(R.id.etTimeValue)
        etCurrentValue = findViewById(R.id.etCurrentValue)
        isHex = switchValueMode?.isChecked ?: false
        switchValueMode?.setOnCheckedChangeListener { _, isChecked ->
            isHex = isChecked
            etCurrentValue?.hint = if (isChecked) {
                "最大电流值(十六进制)【0x00-0x32】每一单位0.04mA"
            } else {
                "最大电流值【0-200】每一单位0.01mA"
            }
        }
        viewModel.autoIncreaseCurrent = switchAutoIncreaseCurrent?.isChecked ?: false
        switchAutoIncreaseCurrent?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.autoIncreaseCurrent = isChecked
        }
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
            R.id.btnStopDevice -> {
                viewModel.stopDevice()
            }
            R.id.btnTakeOffDevice -> {
                viewModel.takeOffDevice()
            }

            R.id.btnFallCurrent -> {
                //600毫秒减少0.08
                //毫安
                viewModel.reduceCurrent()
            }

            R.id.btnRiseCurrent -> {
                //600毫秒增加0.04
                //毫安
                viewModel.increaseCurrent()
            }

            R.id.btnStopChangeCurrent -> {
                viewModel.cancelReduceOrIncrease()
            }

            R.id.btnStartDevice -> {
                //开始设备
                val time = etTimeValue?.text?.toString()?.toIntOrNull() ?: 30
                viewModel.startDevice(time = time)
            }
        }
    }


}