package com.entertech.tes.vr.mode.biomarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class BiomarkerInsightReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biomarker_insight_report)

        findViewById<Button>(R.id.btnOpenTrendFromReport)?.setOnClickListener {
            startActivity(Intent(this, BiomarkerTrendOverviewActivity::class.java))
        }

        findViewById<Button>(R.id.btnCloseReport)?.setOnClickListener {
            finish()
        }
    }
}
