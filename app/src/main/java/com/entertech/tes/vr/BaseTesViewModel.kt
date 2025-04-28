package com.entertech.tes.vr

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.TesDeviceManager
import com.entertech.tes.ble.device.TesDeviceManager.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.ble.device.TesDeviceManager.Companion.PHONE_TO_DEVICE_UUID
import com.entertech.tes.ble.device.byteArrayToHex
import com.entertech.tes.ble.device.byteToHex
import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg
import com.entertech.tes.ble.device.msg.BaseSendTesMsg
import com.entertech.tes.ble.device.msg.control.ControlCommandFdTesMsg
import com.entertech.tes.ble.device.msg.control.ControlCommandFdTesMsg.Companion.CONTROL_RESULT_FAILURE
import com.entertech.tes.ble.device.msg.control.ControlCommandFdTesMsg.Companion.CONTROL_RESULT_SUCCESS
import com.entertech.tes.ble.device.msg.current.IRegulationCurrentFunction.Companion.REGULATION_CURRENT_INCREASE
import com.entertech.tes.ble.device.msg.current.IRegulationCurrentFunction.Companion.REGULATION_CURRENT_REDUCE
import com.entertech.tes.ble.device.msg.current.RegulationCurrentFbTesMsg
import com.entertech.tes.ble.device.msg.current.RegulationCurrentFbTesMsg.Companion.REGULATION_RESULT_DEVICE_IN_REDUCE
import com.entertech.tes.ble.device.msg.current.RegulationCurrentFbTesMsg.Companion.REGULATION_RESULT_FAILURE
import com.entertech.tes.ble.device.msg.current.RegulationCurrentFbTesMsg.Companion.REGULATION_RESULT_SUCCESS
import com.entertech.tes.ble.device.msg.current.RegulationCurrentFbTesMsg.Companion.REGULATION_RESULT_TIME_NOT_ENOUGH
import com.entertech.tes.ble.device.msg.rename.RenameFbTesMsg
import com.entertech.tes.ble.device.msg.rename.RenameFbTesMsg.Companion.RENAME_RESULT_FAILURE
import com.entertech.tes.ble.device.msg.rename.RenameFbTesMsg.Companion.RENAME_RESULT_SUCCESS
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_ERROR
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_FAILURE
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_SUCCESS
import com.entertech.tes.ble.device.msg.shakehand.ShakeHandsFbTesMsg
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_NORMAL
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_STOPING
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_STOP_COMPLETE
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_STOP_FAIL
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_STOP_RECEIVE
import com.entertech.tes.ble.device.msg.version.ReadVersionFbTesMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseTesViewModel : ViewModel() {

    companion object {
        private const val TAG = "BaseTesViewModel"
    }

    protected val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    protected val _deviceInfo = MutableStateFlow("")
    val deviceInfo = _deviceInfo.asStateFlow()

    private val deviceMessageRawDateListener: (ByteArray) -> Unit by lazy {
        { notifyData ->
            val hex = byteArrayToHex(notifyData)
            TesVrLog.d(TAG, "${this.javaClass.simpleName} 接受到的消息 $hex")
            viewModelScope.launch(Dispatchers.Main) {
                _receiveMsg.value = "接受到的消息 $hex"
            }
        }
    }

    private val deviceMessageListener: (BaseReceiveTesMsg?) -> Unit by lazy {
        { processMsg ->
            processMsg(processMsg)
        }
    }


    private val _toastMsg = MutableSharedFlow<String>()
    val toastMsg = _toastMsg.asSharedFlow()
    private val _connectStatus = MutableStateFlow("未连接")
    val connectStatus = _connectStatus.asStateFlow()
    private val _receiveMsg = MutableStateFlow("")
    val receiveMsg = _receiveMsg.asStateFlow()
    private var startServiceIntent: Intent? = null
    protected var tesDeviceManager: TesDeviceManager? = null
    private val connectListener: (String) -> Unit by lazy {
        { mac ->
            TesVrLog.i(
                TAG, "设备连接成功 mac地址：$mac 设备名：${tesDeviceManager?.getDevice()?.getName()}"
            )
            _connectStatus.value = "设备连接状态：已连接 mac地址：$mac 设备名：${
                tesDeviceManager?.getDevice()?.getName()
            }"
            deviceConnected()
        }
    }
    private val disconnectListener: (String) -> Unit by lazy {
        { error ->
            TesVrLog.e(TAG, "设备断开 原因：$error")
            _connectStatus.value = "设备连接状态：未连接"
            tesDeviceManager?.resetDeviceStatus()
            deviceDisconnect()
        }
    }
    private val tesDeviceServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                tesDeviceManager =
                    (p1 as? TesDeviceManagerService.TesServiceBinder)?.getTesDeviceManager()
                addDeviceMessageListener()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                tesDeviceManager = null
            }
        }
    }

    fun addDeviceMessageListener() {
        removeDeviceMessageListener()
        tesDeviceManager?.addDeviceMessageRawDataListener(deviceMessageRawDateListener)
        tesDeviceManager?.addDeviceMessageListener(deviceMessageListener)
    }

    private fun removeDeviceMessageListener() {
        tesDeviceManager?.removeDeviceMessageRawDataListener(deviceMessageRawDateListener)
        tesDeviceManager?.removeDeviceMessageListener(deviceMessageListener)
    }

    fun initTesDeviceManager(createService: Boolean = true, context: Context, intent: Intent) {
        if (createService) {
            startServiceIntent = Intent(context, TesDeviceManagerService::class.java)
            startServiceIntent?.putExtras(intent)
            startServiceIntent?.apply {
                if (!TesDeviceManagerService.isRunning) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(this)
                    } else {
                        context.startService(this)
                    }
                }
                context.bindService(this, tesDeviceServiceConnection, Context.BIND_AUTO_CREATE)
            }
        } else {
            val phoneToDeviceUUid = intent.getStringExtra(PHONE_TO_DEVICE_UUID) ?: ""
            val deviceToPhoneUUid = intent.getStringExtra(DEVICE_TO_PHONE_UUID) ?: ""
            tesDeviceManager =
                TesDeviceManager(TesVrApp.instance, deviceToPhoneUUid, phoneToDeviceUUid)
        }
    }

    fun resetTesDeviceManager(context: Context) {
        if (startServiceIntent != null) {
            context.unbindService(tesDeviceServiceConnection)
            context.stopService(startServiceIntent)
        } else {
            tesDeviceManager = null
        }
    }

    private fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMsg.emit(msg)
        }
    }

    fun sendMessage(send: (TesDeviceManager?) -> Unit) {
        send(tesDeviceManager)
    }

    fun sendMessage(msg: BaseSendTesMsg, needCheckStatus: Boolean = false) {
        tesDeviceManager?.sendMessage(msg, needCheckStatus, { send ->
            TesVrLog.d(TAG, "发送 ${msg.javaClass.simpleName} 指令 成功 ${byteArrayToHex(send)} ")
        }, { errMsg ->
            showToast("发送${msg.javaClass.simpleName} 消息失败 原因： $errMsg")
        })
    }

    fun connectDevice(mac: String) {
        tesDeviceManager?.apply {
            if (isConnected()) {
                showToast("设备已连接")
                return
            }
            if (isConnecting()) {
                showToast("设备正在连接")
                return
            }
            showToast("设备开始连接")
            _connectStatus.value = "设备连接状态：正在连接"
            addTesDeviceListener()
            tesDeviceManager?.connectDeviceByMac(mac)
        } ?: kotlin.run {
            TesVrLog.e(TAG, "tesDeviceManager is null")
        }
    }

    fun connectDeviceByName(name: String) {
        tesDeviceManager?.apply {
            if (isConnected()) {
                showToast("设备已连接")
                return
            }
            if (isConnecting()) {
                showToast("设备正在连接")
                return
            }
            showToast("设备开始连接")
            _connectStatus.value = "设备连接状态：正在连接"
            addTesDeviceListener()
            tesDeviceManager?.connectDeviceByName(name)
        }
    }

    private fun removeTesDeviceListener() {
        tesDeviceManager?.removeConnectListener(connectListener)
        tesDeviceManager?.removeDisConnectListener(disconnectListener)
    }

    open fun deviceDisconnect() {

    }

    open fun deviceConnected() {

    }

    fun setArgAndStart(
        mode: String, time: Int, frequency: Int
    ) {
        tesDeviceManager?.startDevice(mode, time, frequency)
    }

    open fun processMsg(msg: BaseReceiveTesMsg?) {
        when (msg) {
            is UploadTesFbMsg -> {
                val sb = StringBuilder()
                msg.apply {
                    sb.append("当前设定的模式： ${currentMode?.des}\n")
                    sb.append("当前设定的电流： ${setCurrent}mA\n")
                    sb.append("当前设定的时间： ${setTime}min\n")
                    sb.append("当前设定的频率： ${frequency}HZ\n")
                    sb.append("当前刺激剩余时间： $stimulateRemainTime s\n")
                    sb.append("当前阻抗值： $impedanceValue\n")
                    sb.append("当前电流： ${current}mA\n")
                    sb.append("运行状态： $newDeviceStatus [${newDeviceStatus?.des}]\n")
                    sb.append(
                        "停止标记   ${
                            when (stopFlag) {
                                STOP_FLAG_NORMAL -> "正常周期性发送"
                                STOP_FLAG_STOPING -> "正在停止中（正常结束缓降）"
                                STOP_FLAG_STOP_COMPLETE -> " 停止完成"
                                STOP_FLAG_STOP_FAIL -> "停止失败"
                                STOP_FLAG_STOP_RECEIVE -> "停止命令已经接收到，主动缓降停止"
                                else -> "未知停止标记 $stopFlag"
                            }
                        }\n"
                    )
                    sb.append("设定电流的阶段  ： $currentLevel\n")
                    sb.append("设备电量： $deviceBattery%\n")
                    sb.append("电流限制是否放开  0：放开限制1：限制  ： $currentLimit\n")
                }
                viewModelScope.launch {
                    _deviceInfo.value = sb.toString()
                }
                viewModelScope.launch(Dispatchers.Main) {
                    val newDeviceStatus = msg.newDeviceStatus
                    val newDeviceBattery = msg.deviceBattery

                    if (newDeviceStatus != tesDeviceManager?.getDeviceStatus()) {
                        tesDeviceManager?.setDeviceStatus(newDeviceStatus)
                        tesDeviceManager?.shakeHands(viewModelScope)
                    }
                    tesDeviceManager?.battery = newDeviceBattery
                }
                processUploadTesFbMsg(msg)
            }

            is ControlCommandFdTesMsg -> {
                val result = when (msg.controlResult) {
                    CONTROL_RESULT_SUCCESS -> {
                        "控制指令成功"
                    }

                    CONTROL_RESULT_FAILURE -> {
                        "控制指令失败"
                    }

                    else -> {
                        "控制指令未知错误 ${msg.controlResult}"
                    }
                }
                TesVrLog.d(TAG, result)
                showToast(result)
                processControlCommandFdTesMsg(msg)
            }

            is ShakeHandsFbTesMsg -> {
                val newDeviceStatus = msg.deviceStatus
                val newDeviceBattery = msg.deviceBattery
                val newRng = msg.rng
                val deviceInfo = "设备状态 $newDeviceStatus 设备电量 $newDeviceBattery "
                viewModelScope.launch {
                    _deviceInfo.value = deviceInfo
                }
                TesVrLog.d(
                    TAG, "设备状态 $newDeviceStatus 设备电量 $newDeviceBattery 设备rng ${
                        byteToHex(newRng)
                    }"
                )

                viewModelScope.launch(Dispatchers.Main) {
                    if (newDeviceStatus != tesDeviceManager?.getDeviceStatus()) {
                        tesDeviceManager?.setDeviceStatus(newDeviceStatus)
                        tesDeviceManager?.shakeHands(viewModelScope)
                    }
                    tesDeviceManager?.battery = newDeviceBattery
                }
                processShakeHandsFbTesMsg(msg)
            }

            is RenameFbTesMsg -> {
                when (msg.reNameResult) {
                    RENAME_RESULT_SUCCESS -> {
                        TesVrLog.d(TAG, "重命名成功")
                        showToast("重命名成功")
                    }

                    RENAME_RESULT_FAILURE -> {
                        TesVrLog.d(TAG, "重命名失败")
                        showToast("重命名失败")
                    }

                    else -> {
                        TesVrLog.d(TAG, "重命名结果未知： ${msg.reNameResult}")
                        showToast("重命名结果未知： ${msg.reNameResult}")
                    }
                }
            }

            is RegulationCurrentFbTesMsg -> {
                var result = when (msg.regulationType) {
                    REGULATION_CURRENT_INCREASE -> {
                        "当前是操作 增加电流  "
                    }

                    REGULATION_CURRENT_REDUCE -> {
                        "当前是操作 减少电流  "
                    }

                    else -> {
                        "当前是操作电流 未知 ${msg.regulationType}  "
                    }
                }
                result += when (msg.regulationResult) {
                    REGULATION_RESULT_SUCCESS -> {
                        "调节电流成功"
                    }

                    REGULATION_RESULT_TIME_NOT_ENOUGH -> {
                        "剩余时间不足，无法调至到此模式"
                    }

                    REGULATION_RESULT_DEVICE_IN_REDUCE -> {
                        "设备正在缓降阶段无法 调节电流"
                    }

                    REGULATION_RESULT_FAILURE -> {
                        "调节电流失败"
                    }

                    else -> {
                        "调节电流 未知结果： ${msg.regulationResult}"
                    }
                }
                TesVrLog.d(TAG, "RegulationCurrentFbTesMsg :$result")
                processRegulationCurrentFbTesMsg(msg)
            }

            is ReadVersionFbTesMsg -> {
                TesVrLog.d(
                    TAG,
                    "硬件版本号：${msg.hardwareVersion} 软件版本号：${msg.softwareVersion} 协议版本号： ${msg.protocolVersion}"
                )
                showToast("硬件版本号：${msg.hardwareVersion} 软件版本号：${msg.softwareVersion} 协议版本号： ${msg.protocolVersion}")
            }

            is SettingArgFbTesMsg -> {
                val result = when (msg.setArgResult) {
                    SETTING_ARG_RESULT_SUCCESS -> {
                        "设置参数成功"
                    }

                    SETTING_ARG_RESULT_FAILURE -> {
                        "设置参数失败"
                    }

                    SETTING_ARG_RESULT_ERROR -> {
                        "设置参数错误"
                    }

                    else -> {
                        "设置参数未知错误 ${msg.setArgResult}"
                    }
                }
                TesVrLog.d(TAG, result)
                showToast(result)
                processSettingArgFbTesMsg(msg)
            }
        }
    }

    open fun processUploadTesFbMsg(msg: UploadTesFbMsg) {

    }

    open fun processControlCommandFdTesMsg(msg: ControlCommandFdTesMsg) {

    }

    open fun processShakeHandsFbTesMsg(msg: ShakeHandsFbTesMsg) {

    }

    open fun processRegulationCurrentFbTesMsg(msg: RegulationCurrentFbTesMsg) {

    }

    open fun processSettingArgFbTesMsg(msg: SettingArgFbTesMsg) {

    }

    suspend fun saveToStringDataStore(context: Context, key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    fun readFromStringDataStore(
        context: Context, key: String, defaultValue: String = ""
    ): Flow<String> {
        val dataStoreKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey] ?: defaultValue
        }
    }

    fun initPermission(activity: Activity, allPermissionGranted: () -> Unit = {}) {
        val needPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mutableListOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        } else {
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            needPermission.add(Manifest.permission.FOREGROUND_SERVICE)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            needPermission.add(Manifest.permission.FOREGROUND_SERVICE_HEALTH)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            needPermission.add(Manifest.permission.ACTIVITY_RECOGNITION)
            needPermission.add(Manifest.permission.BODY_SENSORS)
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

    private fun addTesDeviceListener() {
        removeTesDeviceListener()
        tesDeviceManager?.addConnectListener(connectListener)
        tesDeviceManager?.addDisConnectListener(disconnectListener)
    }


    override fun onCleared() {
        super.onCleared()
        removeTesDeviceListener()
        removeDeviceMessageListener()
    }

    /**
     * @param current 电流值
     * 将double类型的数值转换为16进制的字符串
     * */
    private fun currentToByte(current: Double): Byte {
        val scaleFactor = 1 / 0.04  // 计算比例因子
        val hexValue = (current * scaleFactor).toInt() // 计算对应的整数值
        return hexValue.toByte() // 返回16进制的整数值
    }


}