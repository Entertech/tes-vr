package com.entertech.tes.vr.mode.biomarker

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class BiomarkerTrendOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biomarker_trend_overview)

        findViewById<Button>(R.id.btnCloseTrend)?.setOnClickListener {
            finish()
        }
    }
}
