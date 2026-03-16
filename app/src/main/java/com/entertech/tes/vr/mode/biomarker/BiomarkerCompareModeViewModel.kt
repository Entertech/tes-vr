package com.entertech.tes.vr.mode.biomarker

import com.entertech.tes.vr.BaseTesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BiomarkerCompareModeViewModel : BaseTesViewModel() {

    companion object {
        private const val DEFAULT_BASELINE_MINUTES = 3
        private const val DEFAULT_STIMULATION_MINUTES = 30
        private const val DEFAULT_POST_MINUTES = 3
        private const val INTERFACE_PENDING_MESSAGE = "接口未接入，等待算法与设备层实现"
    }

    data class BiomarkerUiState(
        val processHint: String = "流程：前测脑波3min -> 电流刺激30min -> 后测脑波3min -> 数据对比",
        val baselineStatus: String = "前测脑波采集（3min）：待执行",
        val stimulationStatus: String = "电流刺激（30min）：待执行",
        val postStatus: String = "后测脑波采集（3min）：待执行",
        val compareStatus: String = "前后测数据对比：待执行"
    )

    private val _uiState = MutableStateFlow(BiomarkerUiState())
    val uiState = _uiState.asStateFlow()

    private var gateway: BiomarkerModeGateway? = null
    private var processConfig: BiomarkerProcessConfig = BiomarkerProcessConfig()

    fun bindGateway(gateway: BiomarkerModeGateway) {
        this.gateway = gateway
    }

    fun updateProcessConfig(config: BiomarkerProcessConfig) {
        processConfig = config
        _uiState.value = _uiState.value.copy(
            processHint = "流程：前测脑波${config.baselineMinutes}min -> 电流刺激${config.stimulationMinutes}min -> 后测脑波${config.postMinutes}min -> 数据对比"
        )
    }

    fun requestOneClickProcess() {
        _uiState.value = _uiState.value.copy(
            processHint = "已触发一键流程接口（当前版本仅保留接口占位，未执行具体算法）"
        )
    }

    fun requestBaselineCollection() {
        _uiState.value = _uiState.value.copy(
            baselineStatus = "前测脑波采集（${processConfig.baselineMinutes}min）：请求中..."
        )
        gateway?.collectBaselineEeg(processConfig.baselineMinutes) { result ->
            mainHandler.post {
                _uiState.value = _uiState.value.copy(
                    baselineStatus = formatResult("前测脑波采集", result)
                )
            }
        } ?: markInterfacePending(StepType.BASELINE)
    }

    fun requestStimulation() {
        _uiState.value = _uiState.value.copy(
            stimulationStatus = "电流刺激（${processConfig.stimulationMinutes}min）：请求中..."
        )
        gateway?.startElectricalStimulation(processConfig.stimulationMinutes) { result ->
            mainHandler.post {
                _uiState.value = _uiState.value.copy(
                    stimulationStatus = formatResult("电流刺激", result)
                )
            }
        } ?: markInterfacePending(StepType.STIMULATION)
    }

    fun requestPostCollection() {
        _uiState.value = _uiState.value.copy(
            postStatus = "后测脑波采集（${processConfig.postMinutes}min）：请求中..."
        )
        gateway?.collectPostEeg(processConfig.postMinutes) { result ->
            mainHandler.post {
                _uiState.value = _uiState.value.copy(
                    postStatus = formatResult("后测脑波采集", result)
                )
            }
        } ?: markInterfacePending(StepType.POST)
    }

    fun requestCompare() {
        _uiState.value = _uiState.value.copy(
            compareStatus = "前后测数据对比：请求中..."
        )
        gateway?.comparePreAndPostEeg { result ->
            mainHandler.post {
                _uiState.value = _uiState.value.copy(
                    compareStatus = formatResult("前后测数据对比", result)
                )
            }
        } ?: markInterfacePending(StepType.COMPARE)
    }

    fun resetProcessState() {
        processConfig = BiomarkerProcessConfig(
            baselineMinutes = DEFAULT_BASELINE_MINUTES,
            stimulationMinutes = DEFAULT_STIMULATION_MINUTES,
            postMinutes = DEFAULT_POST_MINUTES
        )
        _uiState.value = BiomarkerUiState()
    }

    private fun markInterfacePending(stepType: StepType) {
        _uiState.value = when (stepType) {
            StepType.BASELINE -> {
                _uiState.value.copy(
                    baselineStatus = "前测脑波采集（${processConfig.baselineMinutes}min）：$INTERFACE_PENDING_MESSAGE"
                )
            }

            StepType.STIMULATION -> {
                _uiState.value.copy(
                    stimulationStatus = "电流刺激（${processConfig.stimulationMinutes}min）：$INTERFACE_PENDING_MESSAGE"
                )
            }

            StepType.POST -> {
                _uiState.value.copy(
                    postStatus = "后测脑波采集（${processConfig.postMinutes}min）：$INTERFACE_PENDING_MESSAGE"
                )
            }

            StepType.COMPARE -> {
                _uiState.value.copy(compareStatus = "前后测数据对比：$INTERFACE_PENDING_MESSAGE")
            }
        }
    }

    private fun formatResult(prefix: String, result: StepExecutionResult): String {
        val status = if (result.success) "成功" else "失败"
        val record = result.recordId?.let { "，记录ID：$it" } ?: ""
        return "$prefix：$status，${result.message}$record"
    }

    private enum class StepType {
        BASELINE,
        STIMULATION,
        POST,
        COMPARE
    }
}
