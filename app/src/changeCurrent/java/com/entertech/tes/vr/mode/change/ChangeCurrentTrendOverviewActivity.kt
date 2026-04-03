package com.entertech.tes.vr.mode.change

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class ChangeCurrentTrendOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_current_trend_overview)

        findViewById<Button>(R.id.btnRestartFromPrecheck)?.setOnClickListener {
            val intent = Intent(this, ChangeCurrentPrecheckBoardActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnCloseTrend)?.setOnClickListener {
            finish()
        }
    }
}
