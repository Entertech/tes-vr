package com.entertech.tes.vr.mode.mbct

data class MbctCourse(
    val id: String,
    val title: String,
    val subtitle: String,
    val stimulationMinutes: Int
)

object MbctCourseCatalog {
    private const val DEFAULT_STIMULATION_MINUTES = 30
    private const val DEFAULT_SUBTITLE_SUFFIX = "，配合 30min 正常模式刺激"

    private fun course(
        id: String,
        title: String,
        subtitle: String
    ): MbctCourse {
        return MbctCourse(
            id = id,
            title = title,
            subtitle = subtitle + DEFAULT_SUBTITLE_SUFFIX,
            stimulationMinutes = DEFAULT_STIMULATION_MINUTES
        )
    }

    val courses: List<MbctCourse> = listOf(
        course(
            id = "breathing_anchor",
            title = "呼吸觉察",
            subtitle = "专注呼吸与身体起伏"
        ),
        course(
            id = "body_scan",
            title = "身体扫描",
            subtitle = "从头到脚稳定扫描身体感受"
        ),
        course(
            id = "kind_awareness",
            title = "开放觉察",
            subtitle = "维持开放注意与情绪接纳"
        ),
        course(
            id = "three_minute_breathing",
            title = "三分钟呼吸空间",
            subtitle = "分阶段回到当下并整理注意力"
        ),
        course(
            id = "walking_meditation",
            title = "行走冥想",
            subtitle = "在步伐节律中建立稳定觉察"
        ),
        course(
            id = "sound_awareness",
            title = "声音觉察",
            subtitle = "以环境声音为锚点训练开放注意"
        ),
        course(
            id = "thought_observing",
            title = "念头观察",
            subtitle = "观察想法出现与消散，不做评判"
        ),
        course(
            id = "emotion_labeling",
            title = "情绪标记",
            subtitle = "识别并命名当下情绪体验"
        ),
        course(
            id = "self_compassion",
            title = "自我关怀",
            subtitle = "用稳定、温和的态度回应自我体验"
        ),
        course(
            id = "mindful_stretch",
            title = "正念伸展",
            subtitle = "结合轻度身体伸展增强躯体感知"
        ),
        course(
            id = "seated_stability",
            title = "静坐稳定",
            subtitle = "在安静坐姿中培养持续专注"
        ),
        course(
            id = "safe_place",
            title = "安全空间",
            subtitle = "通过意象建立稳定与放松体验"
        ),
        course(
            id = "urge_surfing",
            title = "冲动冲浪",
            subtitle = "观察冲动起伏并保持不卷入"
        ),
        course(
            id = "mountain_meditation",
            title = "高山冥想",
            subtitle = "借助高山意象练习稳固与不动摇"
        ),
        course(
            id = "rain_practice",
            title = "RAIN 练习",
            subtitle = "识别、允许、探查并滋养当下体验"
        ),
        course(
            id = "acceptance_space",
            title = "接纳空间",
            subtitle = "为身体和情绪反应腾出接纳空间"
        ),
        course(
            id = "gratitude_settling",
            title = "感恩安住",
            subtitle = "以感恩主题帮助身心回稳"
        ),
        course(
            id = "sleep_preparation",
            title = "睡前安定",
            subtitle = "降低唤醒水平，适合晚间放松"
        ),
        course(
            id = "mindful_eating",
            title = "正念进食想象",
            subtitle = "训练感官细节觉察与节律体验"
        ),
        course(
            id = "mindful_hearing",
            title = "深度聆听",
            subtitle = "围绕听觉细节维持专注与开放"
        ),
        course(
            id = "loving_kindness",
            title = "慈心练习",
            subtitle = "以善意祝愿扩展情绪调节空间"
        ),
        course(
            id = "open_monitoring",
            title = "开放监测",
            subtitle = "不设单一锚点，整体观察身心变化"
        ),
        course(
            id = "values_alignment",
            title = "价值澄清",
            subtitle = "连接个人价值与当下行动方向"
        )
    )

    fun findById(id: String): MbctCourse? {
        return courses.find { it.id == id }
    }
}
