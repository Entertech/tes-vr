package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class MbctReportListActivity : AppCompatActivity() {

    private var tvReportSummary: TextView? = null
    private var reportContainer: LinearLayout? = null
    private var btnBackDataCenter: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_report_list)
        tvReportSummary = findViewById(R.id.tvReportSummary)
        reportContainer = findViewById(R.id.reportContainer)
        btnBackDataCenter = findViewById(R.id.btnBackDataCenter)

        btnBackDataCenter?.setOnClickListener {
            finish()
        }
        renderSummary()
        renderReports()
    }

    private fun renderSummary() {
        val reports = MbctReportMocks.reports
        val lowRiskCount = reports.count { it.riskLevel.contains("低") }
        val averageFocusScore = if (reports.isEmpty()) {
            0
        } else {
            reports.map { it.focusScore }.average().toInt()
        }
        tvReportSummary?.text = buildString {
            append("演示报表数：")
            append(reports.size)
            append("\n低风险报告：")
            append(lowRiskCount)
            append("\n平均专注评分：")
            append(averageFocusScore)
            append("\n最新报表：")
            append(reports.firstOrNull()?.title ?: "暂无")
        }
    }

    private fun renderReports() {
        val inflater = LayoutInflater.from(this)
        reportContainer?.removeAllViews()
        MbctReportMocks.reports.forEach { report ->
            val itemView = inflater.inflate(R.layout.item_mbct_report, reportContainer, false)
            itemView.findViewById<TextView>(R.id.tvReportTitle).text = report.title
            itemView.findViewById<TextView>(R.id.tvReportMeta).text =
                "${report.createdAt}  |  ${report.courseTitle}  |  ${report.completionLabel}"
            itemView.findViewById<TextView>(R.id.tvReportSummary).text = report.summary
            itemView.findViewById<TextView>(R.id.tvReportScores).text =
                "情绪前后：${report.moodBefore} -> ${report.moodAfter}    专注：${report.focusScore}    放松：${report.relaxScore}    脑波稳定：${report.brainwaveStabilityScore}"
            itemView.findViewById<TextView>(R.id.tvReportKeywords).text =
                report.keywords.joinToString(prefix = "#", separator = "   #")
            itemView.findViewById<Button>(R.id.btnViewReportDetail).setOnClickListener {
                openDetail(report.id)
            }
            itemView.setOnClickListener {
                openDetail(report.id)
            }
            reportContainer?.addView(itemView)
        }
    }

    private fun openDetail(reportId: String) {
        startActivity(
            Intent(this, MbctReportDetailActivity::class.java).putExtra(
                MbctReportDetailActivity.EXTRA_REPORT_ID,
                reportId
            )
        )
    }
}
