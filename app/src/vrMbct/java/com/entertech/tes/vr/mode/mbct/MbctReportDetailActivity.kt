package com.entertech.tes.vr.mode.mbct

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class MbctReportDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPORT_ID = "mbct_report_id"
    }

    private var tvReportTitle: TextView? = null
    private var tvReportMeta: TextView? = null
    private var tvReportSummary: TextView? = null
    private var tvScoreOverview: TextView? = null
    private var tvStimulateSummary: TextView? = null
    private var tvBrainwaveObservation: TextView? = null
    private var tvKeywords: TextView? = null
    private var tvTimeline: TextView? = null
    private var tvSuggestions: TextView? = null
    private var btnBackReportList: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_report_detail)
        tvReportTitle = findViewById(R.id.tvReportTitle)
        tvReportMeta = findViewById(R.id.tvReportMeta)
        tvReportSummary = findViewById(R.id.tvReportSummary)
        tvScoreOverview = findViewById(R.id.tvScoreOverview)
        tvStimulateSummary = findViewById(R.id.tvStimulateSummary)
        tvBrainwaveObservation = findViewById(R.id.tvBrainwaveObservation)
        tvKeywords = findViewById(R.id.tvKeywords)
        tvTimeline = findViewById(R.id.tvTimeline)
        tvSuggestions = findViewById(R.id.tvSuggestions)
        btnBackReportList = findViewById(R.id.btnBackReportList)

        btnBackReportList?.setOnClickListener {
            finish()
        }
        renderReport()
    }

    private fun renderReport() {
        val reportId = intent.getStringExtra(EXTRA_REPORT_ID).orEmpty()
        val report = MbctReportMocks.findById(reportId) ?: MbctReportMocks.reports.first()
        tvReportTitle?.text = report.title
        tvReportMeta?.text =
            "${report.createdAt}\n${report.participantName}  |  ${report.sessionCode}\n${report.courseTitle}  |  ${report.riskLevel}  |  ${report.completionLabel}"
        tvReportSummary?.text = report.summary
        tvScoreOverview?.text = buildString {
            append("情绪负荷：")
            append(report.moodBefore)
            append(" -> ")
            append(report.moodAfter)
            append("\n专注评分：")
            append(report.focusScore)
            append("\n放松评分：")
            append(report.relaxScore)
            append("\n脑波稳定性：")
            append(report.brainwaveStabilityScore)
        }
        tvStimulateSummary?.text = buildString {
            append("刺激模式：")
            append(report.stimulationMode)
            append("\n课程时长：")
            append(report.stimulationMinutes)
            append("min")
            append("\n设定电流：")
            append(report.currentLevel)
            append("\n刺激频率：")
            append(report.frequencyLabel)
            append("\n设备名称：")
            append(report.deviceName)
            append("\n设备地址：")
            append(report.deviceMacMasked)
        }
        tvBrainwaveObservation?.text = report.brainwaveObservation
        tvKeywords?.text = report.keywords.joinToString(prefix = "#", separator = "   #")
        tvTimeline?.text = formatBulletList(report.timeline)
        tvSuggestions?.text = formatBulletList(report.suggestions)
    }

    private fun formatBulletList(lines: List<String>): String {
        return lines.mapIndexed { index, line ->
            "${index + 1}. $line"
        }.joinToString(separator = "\n\n")
    }
}
