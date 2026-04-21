package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.entertech.tes.vr.R
import com.entertech.tes.vr.TesVrApp

class MbctCourseListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COURSE_ID = "mbct_course_id"
        private const val MIN_ITEM_WIDTH_DP = 220
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_course_list)

        val sessionId = intent.getStringExtra(MbctPrepareViewModel.EXTRA_SESSION_ID).orEmpty()
        findViewById<TextView>(R.id.tvSessionHint)?.text =
            "前置 3min 引导与脑电采集已完成，请选择本次 MBCT 冥想课程。所有课程均按 30min 正常模式刺激执行。"

        findViewById<RecyclerView>(R.id.rvCourseList)?.apply {
            layoutManager = GridLayoutManager(
                this@MbctCourseListActivity,
                calculateSpanCount()
            )
            adapter = MbctCourseAdapter(MbctCourseCatalog.courses) { course ->
                openCourse(sessionId, course)
            }
        }
    }

    private fun calculateSpanCount(): Int {
        val screenWidthDp = resources.configuration.screenWidthDp
        return (screenWidthDp / MIN_ITEM_WIDTH_DP).coerceAtLeast(1)
    }

    private fun openCourse(sessionId: String, course: MbctCourse) {
        if (sessionId.isNotEmpty()) {
            MbctRecordStore.appendRecord(
                context = TesVrApp.instance,
                sessionId = sessionId,
                stage = "course_selected",
                payload = mapOf(
                    "courseId" to course.id,
                    "courseTitle" to course.title,
                    "stimulationMinutes" to course.stimulationMinutes
                )
            )
        }
        startActivity(
            Intent(this, MbctSessionActivity::class.java).apply {
                putExtra(MbctPrepareViewModel.EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_COURSE_ID, course.id)
            }
        )
    }
}
