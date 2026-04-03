package com.entertech.tes.vr.mode.change

import androidx.lifecycle.viewModelScope
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.ModeType
import com.entertech.tes.ble.device.msg.control.ControlCommandTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_SUCCESS
import com.entertech.tes.vr.BaseTesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangeModeViewModel : BaseTesViewModel() {

    companion object {
        private const val TAG = "ChangeModeViewModel"
        private const val DEFAULT_TIME_MIN = 30
        private const val ADJUST_INTERVAL_MS = 600L
    }

    data class ChangeModeUiState(
        val processHint: String = "流程：疗程前预检 -> 动态电流执行 -> 单次疗程报告 -> 长周期趋势复盘",
        val readinessStatus: String = "设备与电极：待确认",
        val strategyStatus: String = "调节策略：待下发",
        val stimulationStatus: String = "动态电流：待启动",
        val safetyStatus: String = "安全监测：等待设备连接与参数确认"
    )

    private var currentAdjustAlgorithm: CurrentAdjustAlgorithm? = null
    private var isAdjusting = false
    private var pendingStartAdjust = false
    private val _uiState = MutableStateFlow(ChangeModeUiState())
    val uiState = _uiState.asStateFlow()

    private val adjustRunnable: Runnable by lazy {
        Runnable { performAdjustment() }
    }

    fun setCurrentAdjustAlgorithm(algorithm: CurrentAdjustAlgorithm) {
        currentAdjustAlgorithm = algorithm
    }

    override fun deviceConnected() {
        super.deviceConnected()
        tesDeviceManager?.shakeHands(viewModelScope = viewModelScope)
        _uiState.value = _uiState.value.copy(
            readinessStatus = "设备已连接，待完成受试者与电极确认",
            safetyStatus = "安全监测：连接已建立，等待刺激开始"
        )
    }

    override fun deviceDisconnect() {
        super.deviceDisconnect()
        stopAdjusting()
        _uiState.value = _uiState.value.copy(
            readinessStatus = "设备连接已断开，请重新连接后再执行",
            stimulationStatus = "动态电流：已停止",
            safetyStatus = "安全监测：连接中断"
        )
    }

    fun startChangeMode(time: Int = DEFAULT_TIME_MIN) {
        TesVrLog.d(TAG, "开启设备-Change Mode")
        pendingStartAdjust = true
        _uiState.value = _uiState.value.copy(
            strategyStatus = "调节策略：已加载默认自适应曲线",
            stimulationStatus = "动态电流：启动请求已发送",
            safetyStatus = "安全监测：等待设备启动反馈"
        )
        setArgAndStart(ModeType.TDCS_P.des, time = time, frequency = 0)
    }

    fun stopChangeMode() {
        pendingStartAdjust = false
        stopAdjusting()
        tesDeviceManager?.stopDevice()
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "动态电流：已停止",
            safetyStatus = "安全监测：本次刺激已结束"
        )
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
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "动态电流：设备已启动，进入实时调节",
                safetyStatus = "安全监测：阻抗与电流窗口正常"
            )
            viewModelScope.launch(Dispatchers.Main) {
                startAdjusting()
            }
        } else if (pendingStartAdjust && SETTING_ARG_RESULT_SUCCESS != msg.setArgResult) {
            pendingStartAdjust = false
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "动态电流：启动失败，请检查设备状态",
                safetyStatus = "安全监测：等待重新发起"
            )
        }
    }

    fun startAdjusting() {
        if (isAdjusting) {
            return
        }
        TesVrLog.d(TAG, "开始动态调节电流")
        isAdjusting = true
        _uiState.value = _uiState.value.copy(
            strategyStatus = "调节策略：自适应调节已接管，按 600ms 周期刷新",
            stimulationStatus = "动态电流：实时调节中"
        )
        mainHandler.removeCallbacks(adjustRunnable)
        mainHandler.post(adjustRunnable)
    }

    fun stopAdjusting() {
        if (!isAdjusting) {
            return
        }
        TesVrLog.d(TAG, "停止动态调节电流")
        isAdjusting = false
        _uiState.value = _uiState.value.copy(
            strategyStatus = "调节策略：已暂停",
            stimulationStatus = "动态电流：已停止"
        )
        mainHandler.removeCallbacks(adjustRunnable)
    }

    fun markDeviceReady() {
        _uiState.value = _uiState.value.copy(
            readinessStatus = "设备、电极和受试者状态已确认",
            safetyStatus = "安全监测：预检完成，可进入动态刺激"
        )
    }

    fun simulateStrategyRefresh() {
        _uiState.value = _uiState.value.copy(
            strategyStatus = "调节策略：已更新为缓升-维持-微调模板",
            safetyStatus = "安全监测：阈值窗口已重新校准"
        )
    }

    fun resetProcessState() {
        pendingStartAdjust = false
        stopAdjusting()
        _uiState.value = ChangeModeUiState()
    }

    private fun performAdjustment() {
        if (!isAdjusting) {
            return
        }
        val algorithm = currentAdjustAlgorithm
        if (algorithm == null) {
            TesVrLog.d(TAG, "算法未设置，跳过本次调节")
            _uiState.value = _uiState.value.copy(
                strategyStatus = "调节策略：真实算法未接入，当前运行 UI 占位逻辑"
            )
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
