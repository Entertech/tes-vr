package com.entertech.tes.vr.mode.change

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class ChangeCurrentInsightReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_current_insight_report)

        findViewById<Button>(R.id.btnBackToProcessFromReport)?.setOnClickListener {
            startActivity(Intent(this, ChangeModeActivity::class.java))
        }

        findViewById<Button>(R.id.btnOpenTrendFromReport)?.setOnClickListener {
            startActivity(Intent(this, ChangeCurrentTrendOverviewActivity::class.java))
        }
    }
}
