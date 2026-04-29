package com.entertech.tes.vr.mode.mbct

import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

data class MbctBrainwaveUiState(
    val phaseLabel: String,
    val latestValue: Int,
    val samples: List<Int>
)

enum class MbctBrainwavePhase(
    val label: String,
    private val primaryAmplitude: Double,
    private val secondaryAmplitude: Double,
    private val primaryFrequency: Double,
    private val secondaryFrequency: Double
) {
    PREPARE(
        label = "前置引导脑波采集中",
        primaryAmplitude = 220.0,
        secondaryAmplitude = 90.0,
        primaryFrequency = 0.22,
        secondaryFrequency = 0.06
    ),
    SESSION_READY(
        label = "课程准备阶段脑波采集中",
        primaryAmplitude = 180.0,
        secondaryAmplitude = 75.0,
        primaryFrequency = 0.18,
        secondaryFrequency = 0.05
    ),
    STIMULATION(
        label = "课程刺激阶段脑波采集中",
        primaryAmplitude = 280.0,
        secondaryAmplitude = 120.0,
        primaryFrequency = 0.27,
        secondaryFrequency = 0.09
    ),
    POST_GUIDE(
        label = "后置引导脑波采集中",
        primaryAmplitude = 210.0,
        secondaryAmplitude = 85.0,
        primaryFrequency = 0.2,
        secondaryFrequency = 0.055
    );

    fun nextValue(step: Int): Int {
        val primary = sin(step * primaryFrequency) * primaryAmplitude
        val secondary = sin(step * secondaryFrequency + 1.4) * secondaryAmplitude
        val noise = Random.nextInt(-60, 61)
        return (primary + secondary + noise).roundToInt()
            .coerceIn(MIN_VALUE, MAX_VALUE)
    }

    companion object {
        const val MIN_VALUE = -500
        const val MAX_VALUE = 500
        const val MAX_POINT_COUNT = 120
        const val SAMPLE_INTERVAL_MS = 80L
    }
}
