package com.entertech.tes.vr.mode.change

import androidx.lifecycle.viewModelScope
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.ModeType
import com.entertech.tes.ble.device.msg.control.ControlCommandTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_SUCCESS
import com.entertech.tes.vr.BaseTesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeModeViewModel : BaseTesViewModel() {

    companion object {
        private const val TAG = "ChangeModeViewModel"
        private const val DEFAULT_TIME_MIN = 30
        private const val ADJUST_INTERVAL_MS = 600L
    }

    private var currentAdjustAlgorithm: CurrentAdjustAlgorithm? = null
    private var isAdjusting = false
    private var pendingStartAdjust = false

    private val adjustRunnable: Runnable by lazy {
        Runnable { performAdjustment() }
    }

    fun setCurrentAdjustAlgorithm(algorithm: CurrentAdjustAlgorithm) {
        currentAdjustAlgorithm = algorithm
    }

    override fun deviceConnected() {
        super.deviceConnected()
        tesDeviceManager?.shakeHands(viewModelScope = viewModelScope)
    }

    override fun deviceDisconnect() {
        super.deviceDisconnect()
        stopAdjusting()
    }

    fun startChangeMode(time: Int = DEFAULT_TIME_MIN) {
        TesVrLog.d(TAG, "开启设备-Change Mode")
        pendingStartAdjust = true
        setArgAndStart(ModeType.TDCS_P.des, time = time, frequency = 0)
    }

    fun stopChangeMode() {
        pendingStartAdjust = false
        stopAdjusting()
        tesDeviceManager?.stopDevice()
    }

    fun takeOffDevice() {
        pendingStartAdjust = false
        stopAdjusting()
        tesDeviceManager?.sendMessage(
            ControlCommandTesMsg(ControlCommandTesMsg.CONTROL_COMMAND_POWER_OFF),
            needCheckStatus = false,
            success = {},
            failure = {}
        )
    }

    override fun processSettingArgFbTesMsg(msg: SettingArgFbTesMsg) {
        TesVrLog.d(TAG, "接收到启动设备的反馈消息 ${msg.setArgResult}")
        super.processSettingArgFbTesMsg(msg)
        if (pendingStartAdjust && SETTING_ARG_RESULT_SUCCESS == msg.setArgResult) {
            pendingStartAdjust = false
            viewModelScope.launch(Dispatchers.Main) {
                startAdjusting()
            }
        } else if (pendingStartAdjust && SETTING_ARG_RESULT_SUCCESS != msg.setArgResult) {
            pendingStartAdjust = false
        }
    }

    fun startAdjusting() {
        if (isAdjusting) {
            return
        }
        TesVrLog.d(TAG, "开始动态调节电流")
        isAdjusting = true
        mainHandler.removeCallbacks(adjustRunnable)
        mainHandler.post(adjustRunnable)
    }

    fun stopAdjusting() {
        if (!isAdjusting) {
            return
        }
        TesVrLog.d(TAG, "停止动态调节电流")
        isAdjusting = false
        mainHandler.removeCallbacks(adjustRunnable)
    }

    private fun performAdjustment() {
        if (!isAdjusting) {
            return
        }
        val algorithm = currentAdjustAlgorithm
        if (algorithm == null) {
            TesVrLog.d(TAG, "算法未设置，跳过本次调节")
            scheduleNextAdjust()
            return
        }
        when (algorithm.calculateAdjustment()) {
            AdjustmentResult.Increase -> {
                tesDeviceManager?.increaseCurrent(0x01, {
                    TesVrLog.d(TAG, "增加电流成功")
                }) {
                    TesVrLog.d(TAG, "增加电流失败 $it")
                }
            }

            AdjustmentResult.Decrease -> {
                tesDeviceManager?.reduceCurrent(0x02)
            }

            AdjustmentResult.Keep -> {
                // no-op
            }
        }
        scheduleNextAdjust()
    }

    private fun scheduleNextAdjust() {
        if (isAdjusting) {
            mainHandler.postDelayed(adjustRunnable, ADJUST_INTERVAL_MS)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mainHandler.removeCallbacksAndMessages(null)
    }
}

interface CurrentAdjustAlgorithm {
    fun calculateAdjustment(): AdjustmentResult
}

sealed class AdjustmentResult {
    object Increase : AdjustmentResult()
    object Decrease : AdjustmentResult()
    object Keep : AdjustmentResult()
}
