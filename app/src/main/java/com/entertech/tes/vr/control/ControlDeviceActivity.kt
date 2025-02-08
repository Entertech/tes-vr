package com.entertech.tes.vr.control

import android.content.pm.PackageManager
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.entertech.base.util.DateUtils
import cn.entertech.log.api.LogLevelEnum
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.control.ControlCommandTesMsg
import com.entertech.tes.ble.device.msg.current.IRegulationCurrentFunction.Companion.REGULATION_CURRENT_INCREASE
import com.entertech.tes.ble.device.msg.current.IRegulationCurrentFunction.Companion.REGULATION_CURRENT_REDUCE
import com.entertech.tes.ble.device.msg.current.RegulationCurrentTesMsg
import com.entertech.tes.ble.device.msg.rename.RenameTesMsg
import com.entertech.tes.vr.BaseTesActivity
import com.entertech.tes.vr.R
import com.entertech.tes.vr.control.log.LogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ControlDeviceActivity : BaseTesActivity<ControlDeviceViewModel>(), OnClickListener {
    companion object {
        private const val TAG = "ControlDeviceActivity"
    }

    private val logListener: (LogLevelEnum, String, String, Throwable?) -> Unit by lazy {
        { logLevel, tag, msg, throwable ->
            runOnUiThread {
                adapter.addItem(
                    "${
                        DateUtils.getFormatTime(
                            System.currentTimeMillis(), "HH:mm:ss"
                        )
                    } $msg"
                )
                rvLogs?.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private val adapter by lazy {
        LogAdapter()
    }
    private var btnDeviceStart: Button? = null
    private var btnDeviceStop: Button? = null
    private var btnDeviceDisconnect: Button? = null
    private var btnDevicePowerOff: Button? = null

    private var btnShakeHand: Button? = null
    private var btnRename: Button? = null
    private var btnIncreaseCurrent: Button? = null
    private var btnReadVersion: Button? = null
    private var btnReduceCurrent: Button? = null
    private var btnConnectDevice: Button? = null
    private var etNewDeviceName: EditText? = null
    private var btnSetArgAndStart: Button? = null
    private var btnStopFake: Button? = null
    private var tvDeviceInfo: TextView? = null
    private var btnUp: Button? = null
    private var btnDown: Button? = null
    private var tvReceiveMsg: TextView? = null
    private var tvConnectStatus: TextView? = null
    private var rvLogs: RecyclerView? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.activity_control_device
    }

    override fun initActivityView() {
        super.initActivityView()
        btnShakeHand = findViewById(R.id.btnShakeHand)
        btnDeviceStop = findViewById(R.id.btnDeviceStop)
        btnConnectDevice = findViewById(R.id.btnConnectDevice)
        btnStopFake = findViewById(R.id.btnStopFake)
        btnDeviceDisconnect = findViewById(R.id.btnDeviceDisconnect)
        btnDevicePowerOff = findViewById(R.id.btnDevicePowerOff)
        btnRename = findViewById(R.id.btnRename)
        rvLogs = findViewById(R.id.rvLogs)
        rvLogs?.layoutManager = LinearLayoutManager(this)
        rvLogs?.adapter = adapter
        btnReduceCurrent = findViewById(R.id.btnReduceCurrent)
        btnIncreaseCurrent = findViewById(R.id.btnIncreaseCurrent)
        etNewDeviceName = findViewById(R.id.etNewDeviceName)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvConnectStatus = findViewById(R.id.tvConnectStatus)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        btnReadVersion = findViewById(R.id.btnStartFake)
        btnSetArgAndStart = findViewById(R.id.btnSetArgAndStart)
        btnShakeHand?.setOnClickListener(this)
        btnDeviceStart?.setOnClickListener(this)
        btnDevicePowerOff?.setOnClickListener(this)
        btnDeviceDisconnect?.setOnClickListener(this)
        btnDeviceStop?.setOnClickListener(this)
        btnStopFake?.setOnClickListener(this)
        btnRename?.setOnClickListener(this)
        btnReadVersion?.setOnClickListener(this)
        btnUp?.setOnClickListener(this)
        btnDown?.setOnClickListener(this)
        btnReduceCurrent?.setOnClickListener(this)
        btnConnectDevice?.setOnClickListener(this)
        btnIncreaseCurrent?.setOnClickListener(this)
        btnSetArgAndStart?.setOnClickListener(this)
    }

    override fun initActivityData() {
        super.initActivityData()
        TesVrLog.logListener.add(logListener)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.toastMsg.collect {
                if (it.isNotEmpty()) {
                    Toast.makeText(
                        this@ControlDeviceActivity.applicationContext, it, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.connectStatus.collect {
                if (it.isNotEmpty()) {
                    tvConnectStatus?.text = it
                }
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
            R.id.btnShakeHand -> {
                //握手
                /* viewModel.sendMessage{
                     it?.shakeHand()
                 }*/
            }

            R.id.btnDeviceStop -> {
                viewModel.sendMessage { it?.stopDevice() }
            }

            R.id.btnDeviceDisconnect -> {
                viewModel.sendMessage(
                    ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_DISCONNECT)
                )
            }

            R.id.btnDevicePowerOff -> {
                viewModel.sendMessage(
                    ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_POWER_OFF)
                )
            }

            R.id.btnReduceCurrent -> {
                viewModel.sendMessage(
                    RegulationCurrentTesMsg(0x01, REGULATION_CURRENT_REDUCE), true
                )
            }

            R.id.btnIncreaseCurrent -> {
                viewModel.sendMessage(
                    RegulationCurrentTesMsg(0x01, REGULATION_CURRENT_INCREASE), true
                )
            }


            R.id.btnRename -> {
                //重命名
                viewModel.sendMessage(
                    RenameTesMsg(
                        etNewDeviceName?.text.toString().toByteArray()
                    )
                )
            }

            R.id.btnStartFake -> {
                viewModel.sendMessage { it?.startFakeMode() }
            }

            R.id.btnStopFake -> {
                viewModel.sendMessage { it?.stopFakeMode() }
            }

            R.id.btnSetArgAndStart -> {
                //设置参数并启动
                SetArgDialogFragment { mode, current, time, frequency ->
                    viewModel.sendMessage { it?.startDevice(mode, time, frequency) }
                }.show(supportFragmentManager, "SetArgDialogFragment")
            }

            R.id.btnConnectDevice -> {
                viewModel.connectDevice("D4:AD:20:7E:2A:60")
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isAllGrant = true
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被授予
                    TesVrLog.d(TAG, permissions[i] + " granted.")
                } else {
                    // 权限被拒绝
                    TesVrLog.d(TAG, permissions[i] + " denied.")
                    isAllGrant = false
                }
            }
        }
        if (isAllGrant) {
//            mControlDeviceViewModel.connectDevice()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TesVrLog.logListener.remove(logListener)
        viewModel.resetTesDeviceManager(this)
    }
}