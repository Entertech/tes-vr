package com.entertech.tes.vr

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.entertech.tes.ble.TesVrLog
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


    private var btnShakeHand: Button? = null
    private var btnSendControlCommand: Button? = null
    private var btnRename: Button? = null
    private var btnReadVersion: Button? = null
    private var btnSetArgAndStart: Button? = null
    private var tvDeviceInfo: TextView? = null
    private var btnUp: Button? = null
    private var btnDown: Button? = null
    private var tvReceiveMsg: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_device)
        btnShakeHand = findViewById(R.id.btnShakeHand)
        btnSendControlCommand = findViewById(R.id.btnSendControlCommand)
        btnRename = findViewById(R.id.btnRename)
        tvReceiveMsg = findViewById(R.id.tvReceiveMsg)
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo)
        btnReadVersion = findViewById(R.id.btnReadVersion)
        btnSetArgAndStart = findViewById(R.id.btnSetArgAndStart)
        btnUp = findViewById(R.id.btnUp)
        btnDown = findViewById(R.id.btnDown)
        btnShakeHand?.setOnClickListener(this)
        btnSendControlCommand?.setOnClickListener(this)
        btnRename?.setOnClickListener(this)
        btnReadVersion?.setOnClickListener(this)
        btnUp?.setOnClickListener(this)
        btnDown?.setOnClickListener(this)
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

            R.id.btnSendControlCommand -> {
                //发送控制命令
            }

            R.id.btnRename -> {

            }

            R.id.btnReadVersion -> {
                mControlDeviceViewModel.sendMessage(ReadVersionTesMsg(), intent)
            }

            R.id.btnUp -> {

            }

            R.id.btnDown -> {

            }

            R.id.btnSetArgAndStart -> {
                //设置参数并启动
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