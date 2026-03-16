package com.entertech.tes.vr.mode.biomarker

data class BiomarkerProcessConfig(
    val baselineMinutes: Int = 3,
    val stimulationMinutes: Int = 30,
    val postMinutes: Int = 3,
    val subjectId: String = ""
)

data class StepExecutionResult(
    val success: Boolean,
    val message: String,
    val recordId: String? = null
)

fun interface StepResultCallback {
    fun onResult(result: StepExecutionResult)
}

interface BiomarkerModeGateway {
    fun collectBaselineEeg(durationMinutes: Int, callback: StepResultCallback)
    fun startElectricalStimulation(durationMinutes: Int, callback: StepResultCallback)
    fun collectPostEeg(durationMinutes: Int, callback: StepResultCallback)
    fun comparePreAndPostEeg(callback: StepResultCallback)
}

interface MultiModalFusionAlgorithm {
    fun fuseChannels(signalMatrix: List<List<Double>>, samplingRateHz: Int): List<Double>
}

interface BrainWaveComparisonAlgorithm {
    fun compare(beforeFeatures: List<Double>, afterFeatures: List<Double>): Double
}
