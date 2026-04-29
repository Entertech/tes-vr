package com.entertech.tes.vr.mode.mbct

import androidx.lifecycle.viewModelScope
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.ModeType
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg
import com.entertech.tes.ble.device.msg.set.SettingArgFbTesMsg.Companion.SETTING_ARG_RESULT_SUCCESS
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_STOP_COMPLETE
import com.entertech.tes.ble.device.msg.upload.UploadTesFbMsg.Companion.STOP_FLAG_STOP_FAIL
import com.entertech.tes.vr.BaseTesViewModel
import com.entertech.tes.vr.TesVrApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MbctSessionViewModel : BaseTesViewModel() {

    companion object {
        private const val TAG = "MbctSessionViewModel"
        private const val POST_GUIDE_DURATION_SECONDS = 3 * 60
        private const val SIMULATED_SECOND_DELAY_MS = 20L
        private const val SIMULATION_PREPARE_DELAY_MS = 600L
        private const val SIMULATION_READY_DELAY_MS = 400L
    }

    data class MbctSessionUiState(
        val processHint: String = "流程：课程选择完成 -> tTCS 正常模式刺激 -> 3min 引导与脑电采集 -> 保存本地数据（当前页面按模拟正常流程加速演示）",
        val selectedCourse: String = "未选择课程",
        val stimulationStatus: String = "刺激状态：待开始",
        val postGuideStatus: String = "末次引导与脑电采集：待执行",
        val countdownText: String = "30:00",
        val recordStatus: String = "数据文件：待创建",
        val sessionStatus: String = "会话状态：等待课程执行",
        val startButtonEnabled: Boolean = true,
        val stopButtonEnabled: Boolean = false
    )

    private val _uiState = MutableStateFlow(MbctSessionUiState())
    val uiState = _uiState.asStateFlow()
    private val _brainwaveState = MutableStateFlow(
        MbctBrainwaveUiState(
            phaseLabel = MbctBrainwavePhase.SESSION_READY.label,
            latestValue = 0,
            samples = emptyList()
        )
    )
    val brainwaveState = _brainwaveState.asStateFlow()

    private var selectedCourse: MbctCourse? = null
    private var sessionId: String = ""
    private var stimulationJob: Job? = null
    private var postGuideJob: Job? = null
    private var brainwaveJob: Job? = null
    private var stimulationRunning = false
    private var postGuideStarted = false
    private var hasBoundSession = false
    private var brainwaveStep = 0
    private var currentBrainwavePhase = MbctBrainwavePhase.SESSION_READY

    fun bindSession(sessionId: String, courseId: String) {
        if (hasBoundSession) {
            return
        }
        hasBoundSession = true
        startBrainwaveFeedIfNeeded()
        this.sessionId = sessionId
        selectedCourse = MbctCourseCatalog.findById(courseId)
        val filePath = if (sessionId.isEmpty()) {
            "数据文件：未生成"
        } else {
            "数据文件：${MbctRecordStore.getSessionFile(TesVrApp.instance, sessionId).absolutePath}"
        }
        _uiState.value = _uiState.value.copy(
            selectedCourse = selectedCourse?.let {
                "${it.title} (${it.stimulationMinutes}min)"
            } ?: "课程不存在",
            countdownText = selectedCourse?.let {
                formatCountdown(it.stimulationMinutes * 60)
            } ?: MbctSessionUiState().countdownText,
            recordStatus = filePath,
            sessionStatus = "会话状态：已进入疗程执行页，等待开始模拟流程",
            startButtonEnabled = selectedCourse != null,
            stopButtonEnabled = false
        )
    }

    fun startStimulation() {
        val course = selectedCourse ?: return
        if (stimulationRunning || postGuideStarted) {
            return
        }
        stimulationJob?.cancel()
        postGuideJob?.cancel()
        stimulationRunning = true
        postGuideStarted = false
        currentBrainwavePhase = MbctBrainwavePhase.STIMULATION
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：已提交启动请求，课程 ${course.title}",
            postGuideStatus = "末次引导与脑电采集：待执行",
            countdownText = formatCountdown(course.stimulationMinutes * 60),
            sessionStatus = "会话状态：模拟正常流程启动中",
            startButtonEnabled = false,
            stopButtonEnabled = true
        )
        appendSessionRecord(
            stage = "stimulation_requested",
            payload = mapOf(
                "courseId" to course.id,
                "courseTitle" to course.title,
                "stimulationMinutes" to course.stimulationMinutes
            )
        )
        setArgAndStart(ModeType.TDCS_P.des, time = course.stimulationMinutes, frequency = 0)
        stimulationJob = viewModelScope.launch {
            runSimulatedStimulation(course)
        }
    }

    fun stopStimulation() {
        if (!stimulationRunning) {
            return
        }
        stimulationRunning = false
        stimulationJob?.cancel()
        tesDeviceManager?.stopDevice()
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：已手动停止，准备进入末次引导",
            sessionStatus = "会话状态：刺激已手动结束，转入后置引导",
            stopButtonEnabled = false
        )
        appendSessionRecord(
            stage = "stimulation_stop_requested",
            payload = mapOf(
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value
            )
        )
        startPostGuide("手动停止刺激")
    }

    override fun processSettingArgFbTesMsg(msg: SettingArgFbTesMsg) {
        super.processSettingArgFbTesMsg(msg)
        if (stimulationRunning || postGuideStarted) {
            return
        }
        val course = selectedCourse ?: return
        if (SETTING_ARG_RESULT_SUCCESS == msg.setArgResult) {
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：${course.title} 正在进行，剩余时长将随设备上报刷新",
                countdownText = formatCountdown(course.stimulationMinutes * 60),
                sessionStatus = "会话状态：正常模式刺激中"
            )
            appendSessionRecord(
                stage = "stimulation_started",
                payload = mapOf(
                    "courseId" to course.id,
                    "courseTitle" to course.title,
                    "stimulationMinutes" to course.stimulationMinutes
                )
            )
        } else {
            stimulationRunning = false
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：启动失败，请检查设备参数",
                sessionStatus = "会话状态：等待重新启动"
            )
            appendSessionRecord(
                stage = "stimulation_failed",
                payload = mapOf(
                    "courseId" to course.id,
                    "courseTitle" to course.title
                )
            )
        }
    }

    override fun processUploadTesFbMsg(msg: UploadTesFbMsg) {
        if (stimulationRunning || postGuideStarted) {
            return
        }
        val course = selectedCourse ?: return
        if (!stimulationRunning) {
            return
        }
        if (!postGuideStarted) {
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：${course.title} 进行中，设备剩余 ${msg.stimulateRemainTime}s",
                countdownText = formatCountdown(msg.stimulateRemainTime.coerceAtLeast(0)),
                sessionStatus = "会话状态：刺激执行中，等待结束"
            )
        }
        if (msg.stimulateRemainTime <= 0 || msg.stopFlag == STOP_FLAG_STOP_COMPLETE) {
            stimulationRunning = false
            appendSessionRecord(
                stage = "stimulation_completed",
                payload = mapOf(
                    "courseId" to course.id,
                    "courseTitle" to course.title,
                    "deviceInfo" to deviceInfo.value,
                    "receiveMsg" to receiveMsg.value
                )
            )
            startPostGuide("刺激正常结束")
        } else if (msg.stopFlag == STOP_FLAG_STOP_FAIL) {
            stimulationRunning = false
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：设备停止失败，请人工确认",
                sessionStatus = "会话状态：停止失败"
            )
            appendSessionRecord(
                stage = "stimulation_stop_failed",
                payload = mapOf(
                    "courseId" to course.id,
                    "courseTitle" to course.title,
                    "deviceInfo" to deviceInfo.value,
                    "receiveMsg" to receiveMsg.value
                )
            )
        }
    }

    override fun deviceDisconnect() {
        super.deviceDisconnect()
        stimulationRunning = false
        stimulationJob?.cancel()
        postGuideJob?.cancel()
        postGuideStarted = false
        TesVrLog.e(TAG, "设备断开，终止本次 MBCT 刺激流程")
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：设备连接断开",
            sessionStatus = "会话状态：连接中断，请重新连接后再试",
            startButtonEnabled = true,
            stopButtonEnabled = false
        )
        appendSessionRecord(
            stage = "device_disconnected",
            payload = mapOf(
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value
            )
        )
    }

    private suspend fun runSimulatedStimulation(course: MbctCourse) {
        delay(SIMULATION_PREPARE_DELAY_MS)
        if (!stimulationRunning) {
            return
        }
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：设备预检完成，准备进入正常模式",
            sessionStatus = "会话状态：模拟设备握手与参数校验完成"
        )
        delay(SIMULATION_READY_DELAY_MS)
        if (!stimulationRunning) {
            return
        }
        appendSessionRecord(
            stage = "stimulation_started",
            payload = mapOf(
                "courseId" to course.id,
                "courseTitle" to course.title,
                "stimulationMinutes" to course.stimulationMinutes,
                "simulationMode" to true
            )
        )
        val totalSeconds = course.stimulationMinutes * 60
        for (remaining in totalSeconds downTo 0) {
            if (!stimulationRunning) {
                return
            }
            _uiState.value = _uiState.value.copy(
                stimulationStatus = buildSimulatedStimulationStatus(course.title, totalSeconds, remaining),
                countdownText = formatCountdown(remaining),
                sessionStatus = "会话状态：模拟正常模式刺激中",
                startButtonEnabled = false,
                stopButtonEnabled = true
            )
            if (remaining == 0) {
                break
            }
            delay(SIMULATED_SECOND_DELAY_MS)
        }
        if (!stimulationRunning) {
            return
        }
        stimulationRunning = false
        appendSessionRecord(
            stage = "stimulation_completed",
            payload = mapOf(
                "courseId" to course.id,
                "courseTitle" to course.title,
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value,
                "simulationMode" to true
            )
        )
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：${course.title} 已完成，准备进入后置引导",
            stopButtonEnabled = false
        )
        startPostGuide("模拟刺激正常结束")
    }

    private fun startPostGuide(reason: String) {
        if (postGuideStarted) {
            return
        }
        postGuideStarted = true
        currentBrainwavePhase = MbctBrainwavePhase.POST_GUIDE
        appendSessionRecord(
            stage = "post_guide_started",
            payload = mapOf(
                "reason" to reason,
                "guideMinutes" to 3,
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value
            )
        )
        _uiState.value = _uiState.value.copy(
            postGuideStatus = "末次引导与脑电采集：进行中",
            countdownText = formatCountdown(POST_GUIDE_DURATION_SECONDS),
            sessionStatus = "会话状态：进入疗程后 3min 引导采集",
            startButtonEnabled = false,
            stopButtonEnabled = false
        )
        postGuideJob?.cancel()
        postGuideJob = viewModelScope.launch {
            for (remaining in POST_GUIDE_DURATION_SECONDS downTo 0) {
                _uiState.value = _uiState.value.copy(
                    countdownText = formatCountdown(remaining),
                    postGuideStatus = if (remaining == 0) {
                        "末次引导与脑电采集：完成"
                    } else {
                        "末次引导与脑电采集：进行中"
                    }
                )
                if (remaining == 0) {
                    break
                }
                delay(SIMULATED_SECOND_DELAY_MS)
            }
            postGuideStarted = false
            currentBrainwavePhase = MbctBrainwavePhase.SESSION_READY
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：本次课程流程已完成",
                sessionStatus = "会话状态：本次 VR-MBCT 已完成，数据已保存，可重新开始模拟",
                startButtonEnabled = true,
                stopButtonEnabled = false
            )
            appendSessionRecord(
                stage = "post_guide_completed",
                payload = mapOf(
                    "guideMinutes" to 3,
                    "deviceInfo" to deviceInfo.value,
                    "receiveMsg" to receiveMsg.value
                )
            )
            appendSessionRecord(
                stage = "session_completed",
                payload = mapOf(
                    "courseId" to selectedCourse?.id,
                    "courseTitle" to selectedCourse?.title
                )
            )
        }
    }

    private fun appendSessionRecord(stage: String, payload: Map<String, Any?> = emptyMap()) {
        if (sessionId.isEmpty()) {
            return
        }
        MbctRecordStore.appendRecord(
            context = TesVrApp.instance,
            sessionId = sessionId,
            stage = stage,
            payload = payload
        )
    }

    override fun onCleared() {
        super.onCleared()
        stimulationJob?.cancel()
        postGuideJob?.cancel()
        brainwaveJob?.cancel()
    }

    private fun startBrainwaveFeedIfNeeded() {
        if (brainwaveJob?.isActive == true) {
            return
        }
        brainwaveJob = viewModelScope.launch {
            while (true) {
                updateBrainwave(currentBrainwavePhase)
                delay(MbctBrainwavePhase.SAMPLE_INTERVAL_MS)
            }
        }
    }

    private fun updateBrainwave(phase: MbctBrainwavePhase) {
        brainwaveStep += 1
        val value = phase.nextValue(brainwaveStep)
        val newSamples = (_brainwaveState.value.samples + value)
            .takeLast(MbctBrainwavePhase.MAX_POINT_COUNT)
        _brainwaveState.value = MbctBrainwaveUiState(
            phaseLabel = phase.label,
            latestValue = value,
            samples = newSamples
        )
    }

    private fun buildSimulatedStimulationStatus(
        courseTitle: String,
        totalSeconds: Int,
        remainingSeconds: Int
    ): String {
        if (remainingSeconds == 0) {
            return "刺激状态：$courseTitle 已完成"
        }
        val progress = 1f - remainingSeconds.toFloat() / totalSeconds.toFloat()
        val phase = when {
            progress < 0.2f -> "缓升阶段"
            progress < 0.85f -> "稳定刺激阶段"
            else -> "缓降阶段"
        }
        return "刺激状态：$courseTitle 正常模式刺激中（$phase）"
    }

    private fun formatCountdown(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
