package com.entertech.tes.vr.mode.change

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class ChangeCurrentPrecheckBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_current_precheck_board)

        findViewById<Button>(R.id.btnGoToChangeProcess)?.setOnClickListener {
            startActivity(Intent(this, ChangeModeActivity::class.java))
        }

        findViewById<Button>(R.id.btnClosePrecheck)?.setOnClickListener {
            finish()
        }
    }
}
