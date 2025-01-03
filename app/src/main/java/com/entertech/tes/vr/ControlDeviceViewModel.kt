package com.entertech.tes.vr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import cn.entertech.ble.api.ConnectionBleStrategy
import cn.entertech.ble.uid.characteristic.BluetoothCharacteristic
import cn.entertech.ble.uid.property.BluetoothProperty
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.TesDeviceManager
import com.entertech.tes.ble.device.msg.AnalysisTesMsgTool
import com.entertech.tes.ble.device.msg.BaseSendTesMsg
import com.entertech.tes.vr.MainActivity.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.vr.MainActivity.Companion.PHONE_TO_DEVICE_UUID

class ControlDeviceViewModel : ViewModel() {
    companion object {
        private const val TAG = "ControlDeviceViewModel"
    }

    private var bluetoothCharacteristic: BluetoothCharacteristic? = null

    private val tesDeviceManager by lazy {
        TesDeviceManager(context = TesVrApp.instance)
    }

    fun initPermission(activity: Activity, allPermissionGranted: () -> Unit) {
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
                    activity, needPermission[i]
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
            ActivityCompat.requestPermissions(activity, permissions, 1)
        } else {
            allPermissionGranted()
        }
    }

    fun sendMessage(msg: BaseSendTesMsg, intent: Intent) {
        if (bluetoothCharacteristic?.uid.isNullOrEmpty()) {
            bluetoothCharacteristic = BluetoothCharacteristic(
                intent.getStringExtra(PHONE_TO_DEVICE_UUID) ?: "", listOf(
                    BluetoothProperty.BLUETOOTH_PROPERTY_WRITE
                )
            )
        }
        bluetoothCharacteristic?.apply {
            tesDeviceManager.write(this, bytes = msg.createMsg(), { send ->
                TesVrLog.d(TAG, "write ShakeHandsTesMsg success ${send.contentToString()}")
            }, { errMsg ->
                TesVrLog.e(TAG, "write ShakeHandsTesMsg failure :$errMsg")
            })
        }
    }

    fun connectDevice(intent: Intent) {
        tesDeviceManager.connectDevice(
            {
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
}