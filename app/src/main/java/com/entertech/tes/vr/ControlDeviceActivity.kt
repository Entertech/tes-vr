package com.entertech.tes.vr

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.control.ControlCommandTesMsg
import com.entertech.tes.ble.device.msg.current.IRegulationCurrentFunction
import com.entertech.tes.ble.device.msg.current.RegulationCurrentTesMsg
import com.entertech.tes.ble.device.msg.current.RegulationCurrentTesMsg.Companion.REGULATION_CURRENT_INCREASE
import com.entertech.tes.ble.device.msg.current.RegulationCurrentTesMsg.Companion.REGULATION_CURRENT_REDUCE
import com.entertech.tes.ble.device.msg.rename.RenameTesMsg
import com.entertech.tes.ble.device.msg.version.ReadVersionTesMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ControlDeviceActivity : AppCompatActivity(), OnClickListener {
    companion object {
        private const val TAG = "ControlDeviceActivity"
    }

    private val mControlDeviceViewModel by lazy {
        ViewModelProvider(this)[ControlDeviceViewModel::class.java]
    }

    private var btnDeviceStart: Button? = null
    private var btnDeviceStop: Button? = null
    private var btnDeviceDisconnect: Button? = null
    private var btnDevicePowerOff: Button? = null

    private var btnShakeHand: Button? = null
    private var btnUpload: Button? = null
    private var btnRename: Button? = null
    private var btnIncreaseCurrent: Button? = null
    private var btnReadVersion: Button? = null
    private var etCurrent: EditText? = null
    private var btnReduceCurrent: EditText? = null
    private var etNewDeviceName: EditText? = null
    private var btnSetArgAndStart: Button? = null
    private var tvDeviceInfo: TextView? = null
    private var btnUp: Button? = null
    private var btnDown: Button? = null
    private var tvReceiveMsg: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_device)
        btnShakeHand = findViewById(R.id.btnShakeHand)
        btnDeviceStart = findViewById(R.id.btnDeviceStart)
        btnDeviceStop = findViewById(R.id.btnDeviceStop)
        btnDeviceDisconnect = findViewById(R.id.btnDeviceDisconnect)
        btnDevicePowerOff = findViewById(R.id.btnDevicePowerOff)
        btnUpload = findViewById(R.id.btnUpload)
        btnRename = findViewById(R.id.btnRename)
        etCurrent = findViewById(R.id.etCurrent)
        btnReduceCurrent = findViewById(R.id.btnReduceCurrent)
        btnIncreaseCurrent = findViewById(R.id.btnIncreaseCurrent)
        etNewDeviceName = findViewById(R.id.etNewDeviceName)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        btnReadVersion = findViewById(R.id.btnReadVersion)
        btnSetArgAndStart = findViewById(R.id.btnSetArgAndStart)
        btnShakeHand?.setOnClickListener(this)
        btnDeviceStart?.setOnClickListener(this)
        btnDevicePowerOff?.setOnClickListener(this)
        btnDeviceDisconnect?.setOnClickListener(this)
        btnDeviceStop?.setOnClickListener(this)
        btnUpload?.setOnClickListener(this)
        btnRename?.setOnClickListener(this)
        btnReadVersion?.setOnClickListener(this)
        btnUp?.setOnClickListener(this)
        btnDown?.setOnClickListener(this)
        btnReduceCurrent?.setOnClickListener(this)
        btnIncreaseCurrent?.setOnClickListener(this)
        btnSetArgAndStart?.setOnClickListener(this)
        lifecycleScope.launch(Dispatchers.Main) {
            mControlDeviceViewModel.toastMsg.collect {
                if (it.isNotEmpty()) {
                    Toast.makeText(
                        this@ControlDeviceActivity.applicationContext, it, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            mControlDeviceViewModel.deviceInfo.collect {
                if (it.isNotEmpty()) {
                    tvDeviceInfo?.text = it
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            mControlDeviceViewModel.receiveMsg.collect {
                if (it.isNotEmpty()) {
                    tvReceiveMsg?.text = it
                }
            }
        }

        mControlDeviceViewModel.initPermission(this) {
            mControlDeviceViewModel.connectDevice(intent)
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnShakeHand -> {
                //握手
                mControlDeviceViewModel.shakeHands(intent)
            }

            R.id.btnDeviceStart -> {
                mControlDeviceViewModel.sendMessage(
                    ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_START), intent
                )
            }

            R.id.btnDeviceStop -> {
                mControlDeviceViewModel.sendMessage(
                    ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_STOP), intent
                )
            }

            R.id.btnDeviceDisconnect -> {
                mControlDeviceViewModel.sendMessage(
                    ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_DISCONNECT), intent
                )
            }

            R.id.btnDevicePowerOff -> {
                mControlDeviceViewModel.sendMessage(
                    ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_POWER_OFF), intent
                )
            }

            R.id.btnReduceCurrent -> {
                val current = IRegulationCurrentFunction.currentValueToByte(
                    etCurrent?.text.toString().toDouble()
                )
                mControlDeviceViewModel.sendMessage(
                    RegulationCurrentTesMsg(current, REGULATION_CURRENT_REDUCE), intent
                )
            }

            R.id.btnIncreaseCurrent -> {
                val current = IRegulationCurrentFunction.currentValueToByte(
                    etCurrent?.text.toString().toDouble()
                )
                mControlDeviceViewModel.sendMessage(
                    RegulationCurrentTesMsg(current, REGULATION_CURRENT_INCREASE), intent
                )
            }

            R.id.btnUpload -> {

            }

            R.id.btnRename -> {
                //重命名
                mControlDeviceViewModel.sendMessage(
                    RenameTesMsg(
                        etNewDeviceName?.text.toString().toByteArray()
                    ), intent
                )
            }

            R.id.btnReadVersion -> {
                mControlDeviceViewModel.sendMessage(ReadVersionTesMsg(), intent)
            }

            R.id.btnSetArgAndStart -> {
                //设置参数并启动
                SetArgDialogFragment { mode, current, time, frequency ->
                    mControlDeviceViewModel.setArgAndStart(mode, current, time, frequency, intent)
                }.show(supportFragmentManager, "SetArgDialogFragment")
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
            mControlDeviceViewModel.connectDevice(intent)
        }
    }


}