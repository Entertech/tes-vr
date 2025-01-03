package com.entertech.tes.vr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.entertech.ble.api.ConnectionBleStrategy
import cn.entertech.ble.uid.characteristic.BluetoothCharacteristic
import cn.entertech.ble.uid.property.BluetoothProperty
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.DeviceStatus
import com.entertech.tes.ble.device.TesDeviceManager
import com.entertech.tes.ble.device.msg.AnalysisTesMsgTool
import com.entertech.tes.ble.device.msg.BaseSendTesMsg
import com.entertech.tes.ble.device.msg.shakehand.ShakeHandsFbTesMsg
import com.entertech.tes.ble.device.msg.shakehand.ShakeHandsTesMsg
import com.entertech.tes.ble.device.msg.version.ReadVersionFbTesMsg
import com.entertech.tes.vr.MainActivity.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.vr.MainActivity.Companion.PHONE_TO_DEVICE_UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ControlDeviceViewModel : ViewModel() {
    companion object {
        private const val TAG = "ControlDeviceViewModel"
    }

    private var shakeHandJob: Job? = null

    private var bluetoothCharacteristic: BluetoothCharacteristic? = null

    private val tesDeviceManager by lazy {
        TesDeviceManager(context = TesVrApp.instance)
    }
    private var shakeHandsTesMsg: ShakeHandsTesMsg? = null

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

    fun shakeHands(intent: Intent) {
        shakeHandJob?.cancel()
        if (tesDeviceManager.deviceStatus == DeviceStatus.DEVICE_STATUS_RUNNING) {
            TesVrLog.d(TAG, "设备处于运行中  不用发送握手包")
            return
        }
        val repeatCount: Int
        val msgDelay: Long
        when (tesDeviceManager.deviceStatus) {
            DeviceStatus.DEVICE_STATUS_READY -> {
                repeatCount = Int.MAX_VALUE
                msgDelay = 3000L
            }

            null -> {
                repeatCount = 3
                msgDelay = 1000L
            }

            else -> {
                repeatCount = 0
                msgDelay = 0L
            }
        }
        TesVrLog.d(TAG, "需要发送握手包的次数 $repeatCount")
        if (repeatCount <= 0) {
            return
        }
        shakeHandJob = viewModelScope.launch {
            repeat(repeatCount) {
                if (tesDeviceManager.deviceStatus == DeviceStatus.DEVICE_STATUS_RUNNING) {
                    shakeHandJob?.cancel()
                    TesVrLog.d(TAG, "设备处于运行中  不用发送握手包")
                    return@launch
                }
                if (!isActive) {
                    TesVrLog.d(TAG, "shakeHands job is cancel")
                    return@launch
                }
                if (shakeHandsTesMsg == null) {
                    shakeHandsTesMsg = ShakeHandsTesMsg()
                }
                shakeHandsTesMsg?.apply {
                    sendMessage(this, intent)
                }
                delay(msgDelay)
            }
        }
    }

    fun connectDevice(intent: Intent) {
        tesDeviceManager.connectDevice({
            TesVrLog.d(TAG, "connect mac :$it")
            val deviceToPhoneUUid = intent.getStringExtra(DEVICE_TO_PHONE_UUID) ?: ""

            tesDeviceManager.notify(BluetoothCharacteristic(
                deviceToPhoneUUid, listOf(
                    BluetoothProperty.BLUETOOTH_PROPERTY_NOTIFY
                )
            ), { notifyData ->
                TesVrLog.d(TAG, "notify data :${notifyData.contentToString()}")
                when (val msg = AnalysisTesMsgTool.processMsg(notifyData)) {
                    is ShakeHandsFbTesMsg -> {
                        val newDeviceStatus = msg.deviceStatus
                        val newDeviceBattery = msg.deviceBattery
                        val newRng = msg.rng
                        TesVrLog.d(
                            TAG,
                            "设备状态 $newDeviceStatus 设备电量 $newDeviceBattery 设备rng ${
                                byteToHex(newRng)
                            }"
                        )

                        viewModelScope.launch(Dispatchers.Main) {
                            if (newDeviceStatus != tesDeviceManager.deviceStatus) {
                                tesDeviceManager.deviceStatus = newDeviceStatus
                                shakeHands(intent)
                            }
                            tesDeviceManager.battery = newDeviceBattery

                        }

                    }

                    is ReadVersionFbTesMsg -> {
                        TesVrLog.d(
                            TAG,
                            "硬件版本号：${msg.hardwareVersion} 软件版本号：${msg.softwareVersion} 协议版本号： ${msg.protocolVersion}"
                        )
                    }
                }


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

    private fun byteToHex(byte: Byte?): String {
        if (byte == null) {
            return "null"
        }
        return "0x" + String.format("%02X", byte)
    }

    private fun byteArrayToHex(byteArray: ByteArray): String {
        val sb = StringBuilder()
        for (byte in byteArray) {
            sb.append(byteToHex(byte))
        }
        return sb.toString()
    }
}