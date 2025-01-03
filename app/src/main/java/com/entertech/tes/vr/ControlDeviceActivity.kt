package com.entertech.tes.vr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import cn.entertech.base.BaseActivity
import cn.entertech.ble.api.ConnectionBleStrategy
import cn.entertech.ble.uid.characteristic.BluetoothCharacteristic
import cn.entertech.ble.uid.property.BluetoothProperty
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.TesDeviceManager
import com.entertech.tes.ble.device.msg.AnalysisTesMsgTool
import com.entertech.tes.ble.device.msg.BaseSendTesMsg
import com.entertech.tes.ble.device.msg.shakehand.ShakeHandsTesMsg
import com.entertech.tes.vr.MainActivity.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.vr.MainActivity.Companion.PHONE_TO_DEVICE_UUID


class ControlDeviceActivity : BaseActivity() {
    companion object {
        private const val TAG = "ControlDeviceActivity"
    }

    private val tesDeviceManager by lazy {
        TesDeviceManager(context = applicationContext)
    }

    private var btnShakeHand: Button? = null
    private var btnStop: Button? = null
    private var btnRename: Button? = null
    private var btnReadVersion: Button? = null
    private var btnUp: Button? = null
    private var btnDown: Button? = null
    private var bluetoothCharacteristic: BluetoothCharacteristic? = null
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
        initPermission()

    }

    private fun sendMessage(msg: BaseSendTesMsg) {
        if (bluetoothCharacteristic?.uid.isNullOrEmpty()) {
            bluetoothCharacteristic = BluetoothCharacteristic(
                intent.getStringExtra(PHONE_TO_DEVICE_UUID) ?: "", listOf(
                    BluetoothProperty.BLUETOOTH_PROPERTY_WRITE
                )
            )
        }
        bluetoothCharacteristic?.apply {
            tesDeviceManager.write(this, bytes = msg.createMsg(), {
                TesVrLog.d(TAG, "write ShakeHandsTesMsg success")
            }, { errMsg ->
                TesVrLog.e(TAG, "write ShakeHandsTesMsg failure :$errMsg")
            })
        }


    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btnShakeHand -> {
                //握手
                sendMessage(ShakeHandsTesMsg())
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

    private fun initPermission() {
        val needPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        val needRequestPermissions = ArrayList<String>()
        for (i in needPermission.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this, needPermission[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestPermissions.add(needPermission[i])
            }
        }
        if (needRequestPermissions.size != 0) {
            val permissions = arrayOfNulls<String>(needRequestPermissions.size)
            for (i in needRequestPermissions.indices) {
                permissions[i] = needRequestPermissions[i]
            }
            ActivityCompat.requestPermissions(this, permissions, 1)
        } else {
            connectDevice()
        }
    }

    private fun connectDevice() {
        tesDeviceManager.connectDevice({
            TesVrLog.d(TAG, "connect mac :$it")
            val deviceToPhoneUUid = intent.getStringExtra(DEVICE_TO_PHONE_UUID) ?: ""

            tesDeviceManager.notify(BluetoothCharacteristic(
                deviceToPhoneUUid, listOf(
                    BluetoothProperty.BLUETOOTH_PROPERTY_NOTIFY
                )
            ), { notifyData ->
                TesVrLog.d(TAG, "notify data :${notifyData.contentToString()}")
                val msg = AnalysisTesMsgTool.processMsg(notifyData)

            }, { errMsg ->
                TesVrLog.d(TAG, "notify data error $errMsg")
            })

        },
            {
                TesVrLog.d(TAG, "connect failure :$it")
            },
            connectionBleStrategy = ConnectionBleStrategy.CONNECT_DEVICE_MAC,
            mac = "D4:AD:20:74:75:FC"
        )
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
            connectDevice()
        }
    }


}