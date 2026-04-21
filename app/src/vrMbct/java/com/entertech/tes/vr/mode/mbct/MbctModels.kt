package com.entertech.tes.vr.mode.mbct

data class MbctCourse(
    val id: String,
    val title: String,
    val subtitle: String,
    val stimulationMinutes: Int
)

object MbctCourseCatalog {
    val courses: List<MbctCourse> = listOf(
        MbctCourse(
            id = "breathing_anchor",
            title = "呼吸觉察",
            subtitle = "专注呼吸与身体起伏，配合 20min 正常模式刺激",
            stimulationMinutes = 20
        ),
        MbctCourse(
            id = "body_scan",
            title = "身体扫描",
            subtitle = "从头到脚稳定扫描身体感受，配合 25min 正常模式刺激",
            stimulationMinutes = 25
        ),
        MbctCourse(
            id = "kind_awareness",
            title = "开放觉察",
            subtitle = "维持开放注意与情绪接纳，配合 30min 正常模式刺激",
            stimulationMinutes = 30
        )
    )

    fun findById(id: String): MbctCourse? {
        return courses.find { it.id == id }
    }
}
