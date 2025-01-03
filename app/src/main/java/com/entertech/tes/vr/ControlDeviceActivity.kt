package com.entertech.tes.vr

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.msg.shakehand.ShakeHandsTesMsg


class ControlDeviceActivity : AppCompatActivity(), OnClickListener {
    companion object {
        private const val TAG = "ControlDeviceActivity"
    }

    private val mControlDeviceViewModel by lazy {
        ViewModelProvider(this)[ControlDeviceViewModel::class.java]
    }


    private var btnShakeHand: Button? = null
    private var btnStop: Button? = null
    private var btnRename: Button? = null
    private var btnReadVersion: Button? = null
    private var btnUp: Button? = null
    private var btnDown: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_device)
        btnShakeHand = findViewById(R.id.btnShakeHand)
        btnStop = findViewById(R.id.btnStop)
        btnRename = findViewById(R.id.btnRename)
        btnReadVersion = findViewById(R.id.btnReadVersion)
        btnUp = findViewById(R.id.btnUp)
        btnDown = findViewById(R.id.btnDown)
        btnShakeHand?.setOnClickListener(this)
        btnStop?.setOnClickListener(this)
        btnRename?.setOnClickListener(this)
        btnReadVersion?.setOnClickListener(this)
        btnUp?.setOnClickListener(this)
        btnDown?.setOnClickListener(this)
        mControlDeviceViewModel.initPermission(this) {
            mControlDeviceViewModel.connectDevice(intent)
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnShakeHand -> {
                //握手
                mControlDeviceViewModel.sendMessage(ShakeHandsTesMsg(), intent)
            }

            R.id.btnStop -> {

            }

            R.id.btnRename -> {

            }

            R.id.btnReadVersion -> {

            }

            R.id.btnUp -> {

            }

            R.id.btnDown -> {

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