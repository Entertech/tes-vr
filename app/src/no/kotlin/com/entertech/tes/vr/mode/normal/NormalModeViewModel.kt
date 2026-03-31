package com.entertech.tes.vr.mode.normal

import androidx.lifecycle.viewModelScope
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.ModeType
import com.entertech.tes.ble.device.msg.control.ControlCommandTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_SUCCESS
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg
import com.entertech.tes.vr.BaseTesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NormalModeViewModel : BaseTesViewModel() {

    companion object {
        private const val DEFAULT_MAX_CURRENT = 2.0
        private const val INCREASE_CURRENT_STEP = 0.04
        private const val TAG = "NormalModeViewModel"
    }

    var autoIncreaseCurrent = false

    /**
     * 增加电流的标志
     * */
    private var increaseCurrentFlag = false

    /**
     * 减少电流的标志
     * */
    private var reduceCurrentFlag = false
    private val increaseRunnable: Runnable by lazy {
        Runnable {
            increaseCurrent()
        }
    }

    private val reduceRunnable: Runnable by lazy {
        Runnable {
            reduceCurrent()
        }
    }

    override fun deviceConnected() {
        super.deviceConnected()
        tesDeviceManager?.shakeHands(viewModelScope = viewModelScope)
    }

    fun reduceCurrent() {
        increaseCurrentFlag = false
        reduceCurrentFlag = true
        cancelReduceOrIncrease()
        tesDeviceManager?.reduceCurrent(0x02)
        mainHandler.postDelayed(reduceRunnable, 600)
    }

    fun cancelReduceOrIncrease() {
        mainHandler.removeCallbacks(reduceRunnable)
        mainHandler.removeCallbacks(increaseRunnable)
    }

    fun increaseCurrent() {
        TesVrLog.d(TAG, "开始增加电流")
        increaseCurrentFlag = true
        reduceCurrentFlag = false
        cancelReduceOrIncrease()
        tesDeviceManager?.increaseCurrent(0x01, {
            TesVrLog.d(TAG, "增加电流成功")
        }) {
            TesVrLog.d(TAG, "增加电流失败 $it")
        }
        mainHandler.postDelayed(increaseRunnable, 600)
    }

    override fun processUploadTesFbMsg(msg: UploadTesFbMsg) {
        if (msg.current >= DEFAULT_MAX_CURRENT && increaseCurrentFlag) {
            TesVrLog.d(TAG, "停止发送增加电流指令")
            mainHandler.removeCallbacks(increaseRunnable)
            mainHandler.post {
                increaseCurrentFlag = false
            }
        }
        if (msg.current <= 0 && reduceCurrentFlag) {
            TesVrLog.d(TAG, "停止发送减少电流指令")
            mainHandler.removeCallbacks(reduceRunnable)
            mainHandler.post {
                reduceCurrentFlag = false
            }
        }
    }

    fun stopDevice() {
        mainHandler.removeCallbacksAndMessages(null)
        tesDeviceManager?.stopDevice()
    }

    fun takeOffDevice() {
        mainHandler.removeCallbacksAndMessages(null)
        tesDeviceManager?.sendMessage(ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_POWER_OFF),
            needCheckStatus = false,
            success = {},
            failure = {})
    }

    override fun processSettingArgFbTesMsg(msg: SettingArgFbTesMsg) {
        TesVrLog.d(TAG, "接收到启动设备的反馈消息 ${msg.setArgResult}")
        super.processSettingArgFbTesMsg(msg)
        if (autoIncreaseCurrent && SETTING_ARG_RESULT_SUCCESS == msg.setArgResult) {
            viewModelScope.launch(Dispatchers.Main) {
                increaseCurrent()
            }
        }
    }

    fun startDevice(time: Int) {
        TesVrLog.d(TAG, "开启设备")
        setArgAndStart(
            ModeType.TDCS_P.des, time = time, frequency = 0
        )
    }

    private fun hexToDecimal(hex: String): Double {
        if (hex.startsWith("0x")) {
            return hex.substring(2).toInt(16).toDouble()
        }
        // 将 16 进制字符串转换为 10 进制数
        // 乘以单元值 0.04 进行转换
        return hex.toInt(16) * 0.04
    }


    override fun onCleared() {
        super.onCleared()
        mainHandler.removeCallbacksAndMessages(null)
    }
}