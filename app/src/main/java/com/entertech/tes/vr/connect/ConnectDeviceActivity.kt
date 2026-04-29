package com.entertech.tes.vr.connect

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import com.entertech.tes.vr.mode.ChooseModeActivity
import kotlinx.coroutines.launch
import java.util.Locale

class ConnectDeviceActivity : BaseTesActivity<ConnectDeviceViewModel>() {

    companion object {
        private val MAC_ADDRESS_REGEX = Regex("^([0-9A-F]{2}:){5}[0-9A-F]{2}$")
    }

    private var tvConnectStatus: TextView? = null
    private var btnConnectDeviceByMac: Button? = null
    private var etDeviceMac: EditText? = null
    private var btnConnectDeviceByName: Button? = null
    private var etDeviceName: EditText? = null
    private var btnOpenModeCenter: Button? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.connect_device_activity
    }

    override fun initActivityView() {
        super.initActivityView()
        tvConnectStatus = findViewById(R.id.tvConnectStatus)
        btnConnectDeviceByMac = findViewById(R.id.btnConnectDeviceByMac)
        etDeviceMac = findViewById(R.id.etDeviceMac)
        btnConnectDeviceByName = findViewById(R.id.btnConnectDeviceByName)
        etDeviceName = findViewById(R.id.etDeviceName)
        btnOpenModeCenter = findViewById(R.id.btnOpenModeCenter)
        btnConnectDeviceByMac?.setOnClickListener(this)
        btnConnectDeviceByName?.setOnClickListener(this)
        btnOpenModeCenter?.setOnClickListener(this)
    }

    override fun initActivityData() {
        super.initActivityData()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.readFromStringDataStore(
                    this@ConnectDeviceActivity, "device_mac", "D4:AD:20:7E:2A:60"
                ).collect {
                    etDeviceMac?.setText(it)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.readFromStringDataStore(
                    this@ConnectDeviceActivity, "device_name", "NE-200A2408403"
                ).collect {
                    etDeviceName?.setText(it)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.connectStatus.collect {
                    tvConnectStatus?.text = it.ifEmpty { "设备连接状态：待连接" }
                    updateButtonState(it.contains("正在连接"))
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.toastMsg.collect {
                    if (it.isNotEmpty()) {
                        Toast.makeText(this@ConnectDeviceActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.connectedStatus.collect {
                    openModeCenter()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnConnectDeviceByMac -> {
                submitConnectByMac()
            }

            R.id.btnConnectDeviceByName -> {
                submitConnectByName()
            }

            R.id.btnOpenModeCenter -> {
                if (viewModel.isDeviceConnected()) {
                    openModeCenter()
                } else {
                    Toast.makeText(
                        this,
                        "设备尚未连接，请先通过 MAC 地址或设备名称连接",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun submitConnectByMac() {
        if (viewModel.isDeviceConnected()) {
            openModeCenter()
            return
        }
        val mac = normalizeMac(etDeviceMac?.text?.toString().orEmpty())
        if (!MAC_ADDRESS_REGEX.matches(mac)) {
            Toast.makeText(
                this,
                "请输入正确的 MAC 地址，例如 D4:AD:20:7E:2A:60",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        etDeviceMac?.setText(mac)
        lifecycleScope.launch {
            viewModel.saveToStringDataStore(this@ConnectDeviceActivity, "device_mac", mac)
        }
        viewModel.connectDevice(mac)
    }

    private fun submitConnectByName() {
        if (viewModel.isDeviceConnected()) {
            openModeCenter()
            return
        }
        val name = etDeviceName?.text?.toString().orEmpty().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "请输入设备名称，例如 NE-200A2408403", Toast.LENGTH_SHORT).show()
            return
        }
        etDeviceName?.setText(name)
        lifecycleScope.launch {
            viewModel.saveToStringDataStore(this@ConnectDeviceActivity, "device_name", name)
        }
        viewModel.connectDeviceByName(name)
    }

    private fun normalizeMac(rawMac: String): String {
        return rawMac.trim()
            .replace("：", ":")
            .replace("-", ":")
            .replace(" ", "")
            .uppercase(Locale.getDefault())
    }

    private fun openModeCenter() {
        val modeIntent = Intent(this@ConnectDeviceActivity, ChooseModeActivity::class.java)
        modeIntent.putExtras(intent)
        startActivity(modeIntent)
    }

    private fun updateButtonState(isConnecting: Boolean) {
        btnConnectDeviceByMac?.isEnabled = !isConnecting
        btnConnectDeviceByName?.isEnabled = !isConnecting
        btnConnectDeviceByMac?.alpha = if (isConnecting) 0.6f else 1f
        btnConnectDeviceByName?.alpha = if (isConnecting) 0.6f else 1f
    }
}
