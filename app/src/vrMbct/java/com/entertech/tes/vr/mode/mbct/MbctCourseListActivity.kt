package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R
import com.entertech.tes.vr.TesVrApp

class MbctCourseListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COURSE_ID = "mbct_course_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_course_list)

        val sessionId = intent.getStringExtra(MbctPrepareViewModel.EXTRA_SESSION_ID).orEmpty()
        findViewById<TextView>(R.id.tvSessionHint)?.text =
            "前置 3min 引导与脑电采集已完成，请选择本次 MBCT 冥想课程。"

        bindCourseButton(
            buttonId = R.id.btnCourseBreathing,
            titleId = R.id.tvCourseBreathingTitle,
            subtitleId = R.id.tvCourseBreathingSubtitle,
            course = MbctCourseCatalog.courses[0],
            sessionId = sessionId
        )
        bindCourseButton(
            buttonId = R.id.btnCourseBodyScan,
            titleId = R.id.tvCourseBodyScanTitle,
            subtitleId = R.id.tvCourseBodyScanSubtitle,
            course = MbctCourseCatalog.courses[1],
            sessionId = sessionId
        )
        bindCourseButton(
            buttonId = R.id.btnCourseOpenAwareness,
            titleId = R.id.tvCourseOpenAwarenessTitle,
            subtitleId = R.id.tvCourseOpenAwarenessSubtitle,
            course = MbctCourseCatalog.courses[2],
            sessionId = sessionId
        )
    }

    private fun bindCourseButton(
        buttonId: Int,
        titleId: Int,
        subtitleId: Int,
        course: MbctCourse,
        sessionId: String
    ) {
        findViewById<TextView>(titleId)?.text = course.title
        findViewById<TextView>(subtitleId)?.text = course.subtitle
        findViewById<Button>(buttonId)?.setOnClickListener {
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
}
