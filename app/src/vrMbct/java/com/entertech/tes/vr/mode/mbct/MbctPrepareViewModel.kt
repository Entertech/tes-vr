package com.entertech.tes.vr.mode.mbct

import androidx.lifecycle.viewModelScope
import com.entertech.tes.vr.BaseTesViewModel
import com.entertech.tes.vr.TesVrApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MbctPrepareViewModel : BaseTesViewModel() {

    companion object {
        const val EXTRA_SESSION_ID = "mbct_session_id"
        private const val GUIDE_DURATION_SECONDS = 3 * 60
    }

    data class MbctPrepareUiState(
        val processHint: String = "流程：连接设备后先完成 3min 引导和脑电采集，然后进入 MBCT 冥想课程列表",
        val guideStatus: String = "前置引导与脑电采集：待开始",
        val countdownText: String = "03:00"
    )

    private val _uiState = MutableStateFlow(MbctPrepareUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var prepareJob: Job? = null
    private var hasStarted = false
    private var sessionId: String = ""

    fun startPrepareGuideIfNeeded() {
        if (hasStarted) {
            return
        }
        hasStarted = true
        sessionId = MbctRecordStore.createSessionId()
        MbctRecordStore.appendRecord(
            context = TesVrApp.instance,
            sessionId = sessionId,
            stage = "prepare_started",
            payload = mapOf(
                "guideMinutes" to 3,
                "deviceInfo" to deviceInfo.value,
                "receiveMsg" to receiveMsg.value
            )
        )
        prepareJob?.cancel()
        prepareJob = viewModelScope.launch {
            for (remaining in GUIDE_DURATION_SECONDS downTo 0) {
                _uiState.value = _uiState.value.copy(
                    guideStatus = if (remaining == 0) {
                        "前置引导与脑电采集：完成"
                    } else {
                        "前置引导与脑电采集：进行中"
                    },
                    countdownText = formatCountdown(remaining)
                )
                if (remaining == 0) {
                    break
                }
                delay(1000)
            }
            MbctRecordStore.appendRecord(
                context = TesVrApp.instance,
                sessionId = sessionId,
                stage = "prepare_completed",
                payload = mapOf(
                    "guideMinutes" to 3,
                    "deviceInfo" to deviceInfo.value,
                    "receiveMsg" to receiveMsg.value
                )
            )
            _navigationEvent.emit(sessionId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        prepareJob?.cancel()
    }

    private fun formatCountdown(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
