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
    }

    data class MbctSessionUiState(
        val processHint: String = "流程：课程选择完成 -> tTCS 正常模式刺激 -> 3min 引导与脑电采集 -> 保存本地数据",
        val selectedCourse: String = "未选择课程",
        val stimulationStatus: String = "刺激状态：待开始",
        val postGuideStatus: String = "末次引导与脑电采集：待执行",
        val countdownText: String = "03:00",
        val recordStatus: String = "数据文件：待创建",
        val sessionStatus: String = "会话状态：等待课程执行"
    )

    private val _uiState = MutableStateFlow(MbctSessionUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedCourse: MbctCourse? = null
    private var sessionId: String = ""
    private var postGuideJob: Job? = null
    private var stimulationRunning = false
    private var postGuideStarted = false
    private var hasBoundSession = false

    fun bindSession(sessionId: String, courseId: String) {
        if (hasBoundSession) {
            return
        }
        hasBoundSession = true
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
            recordStatus = filePath,
            sessionStatus = "会话状态：已进入疗程执行页"
        )
    }

    fun startStimulation() {
        val course = selectedCourse ?: return
        if (stimulationRunning) {
            return
        }
        stimulationRunning = true
        postGuideStarted = false
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：已发送启动请求，课程 ${course.title}",
            sessionStatus = "会话状态：等待设备启动正常模式刺激"
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
    }

    fun stopStimulation() {
        if (!stimulationRunning) {
            return
        }
        tesDeviceManager?.stopDevice()
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：已发送停止请求",
            sessionStatus = "会话状态：等待设备结束后进入末次引导"
        )
        appendSessionRecord(
            stage = "stimulation_stop_requested",
            payload = mapOf(
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value
            )
        )
    }

    override fun processSettingArgFbTesMsg(msg: SettingArgFbTesMsg) {
        super.processSettingArgFbTesMsg(msg)
        val course = selectedCourse ?: return
        if (SETTING_ARG_RESULT_SUCCESS == msg.setArgResult) {
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：${course.title} 正在进行，剩余时长将随设备上报刷新",
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
        val course = selectedCourse ?: return
        if (!stimulationRunning) {
            return
        }
        if (!postGuideStarted) {
            _uiState.value = _uiState.value.copy(
                stimulationStatus = "刺激状态：${course.title} 进行中，设备剩余 ${msg.stimulateRemainTime}s",
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
        TesVrLog.e(TAG, "设备断开，终止本次 MBCT 刺激流程")
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "刺激状态：设备连接断开",
            sessionStatus = "会话状态：连接中断，请重新连接后再试"
        )
        appendSessionRecord(
            stage = "device_disconnected",
            payload = mapOf(
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value
            )
        )
    }

    private fun startPostGuide(reason: String) {
        if (postGuideStarted) {
            return
        }
        postGuideStarted = true
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
            sessionStatus = "会话状态：进入疗程后 3min 引导采集"
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
                delay(1000)
            }
            _uiState.value = _uiState.value.copy(
                sessionStatus = "会话状态：本次 VR-MBCT 已完成，数据已保存"
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
        postGuideJob?.cancel()
    }

    private fun formatCountdown(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
