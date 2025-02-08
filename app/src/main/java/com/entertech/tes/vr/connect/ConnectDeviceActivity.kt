package com.entertech.tes.vr.connect

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cn.entertech.base.BaseActivity
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import com.entertech.tes.vr.mode.ChooseModeActivity
import kotlinx.coroutines.launch

class ConnectDeviceActivity : BaseTesActivity<ConnectDeviceViewModel>() {

    private var btnConnectDeviceByMac: Button? = null
    private var etDeviceMac: EditText? = null
    private var btnConnectDeviceByName: Button? = null
    private var etDeviceName: EditText? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.connect_device_activity
    }

    override fun initActivityView() {
        btnConnectDeviceByMac = findViewById(R.id.btnConnectDeviceByMac)
        etDeviceMac = findViewById(R.id.etDeviceMac)
        btnConnectDeviceByName = findViewById(R.id.btnConnectDeviceByName)
        etDeviceName = findViewById(R.id.etDeviceName)
        btnConnectDeviceByMac?.setOnClickListener(this)
        btnConnectDeviceByName?.setOnClickListener(this)
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
                viewModel.connectedStatus.collect {
                    val intent1 = Intent(this@ConnectDeviceActivity, ChooseModeActivity::class.java)
                    intent1.putExtras(intent)
                    startActivity(intent1)
                }
            }
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnConnectDeviceByMac -> {
                val mac = etDeviceMac?.text.toString()
                lifecycleScope.launch {
                    viewModel.saveToStringDataStore(this@ConnectDeviceActivity, "device_mac", mac)
                }
                viewModel.connectDevice(mac)
            }

            R.id.btnConnectDeviceByName -> {
                val name = etDeviceName?.text.toString()
                lifecycleScope.launch {
                    viewModel.saveToStringDataStore(this@ConnectDeviceActivity, "device_name", name)
                }
                viewModel.connectDeviceByName(name)
            }
        }
    }

}