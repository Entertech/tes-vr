package com.entertech.tes.vr.mode.mbct

data class MbctReport(
    val id: String,
    val title: String,
    val createdAt: String,
    val courseTitle: String,
    val participantName: String,
    val sessionCode: String,
    val summary: String,
    val completionLabel: String,
    val riskLevel: String,
    val moodBefore: Int,
    val moodAfter: Int,
    val focusScore: Int,
    val relaxScore: Int,
    val brainwaveStabilityScore: Int,
    val stimulationMode: String,
    val stimulationMinutes: Int,
    val currentLevel: String,
    val frequencyLabel: String,
    val deviceName: String,
    val deviceMacMasked: String,
    val brainwaveObservation: String,
    val keywords: List<String>,
    val timeline: List<String>,
    val suggestions: List<String>
)

object MbctReportMocks {

    val reports: List<MbctReport> = listOf(
        MbctReport(
            id = "report_20260428_01",
            title = "第 1 周稳定训练报告",
            createdAt = "2026-04-28 09:40",
            courseTitle = "呼吸觉察",
            participantName = "李明轩",
            sessionCode = "VRM-20260428-0940",
            summary = "本次训练流程完整，前置引导、课程刺激和后置引导均按标准路径完成。刺激后情绪紧张评分下降，专注维持表现提升。",
            completionLabel = "完成度 100%",
            riskLevel = "低风险",
            moodBefore = 72,
            moodAfter = 46,
            focusScore = 84,
            relaxScore = 81,
            brainwaveStabilityScore = 79,
            stimulationMode = "tTCS 正常模式",
            stimulationMinutes = 30,
            currentLevel = "1.2mA",
            frequencyLabel = "0Hz",
            deviceName = "NE-200A2408403",
            deviceMacMasked = "D4:AD:20:**:**:60",
            brainwaveObservation = "前置引导阶段波形振幅中等，进入课程后节律逐渐稳定，后置引导阶段波动收敛，整体呈现较好的沉浸式训练响应。",
            keywords = listOf("呼吸锚定", "稳定专注", "睡眠改善"),
            timeline = listOf(
                "09:04 创建会话并完成设备握手",
                "09:05 进入前置 3min 引导与脑波采集",
                "09:08 选择《呼吸觉察》课程并开始正常模式刺激",
                "09:38 刺激结束，进入后置 3min 引导",
                "09:41 会话完成并保存本地数据"
            ),
            suggestions = listOf(
                "建议继续保持每周 3 次以上训练频率。",
                "下次可尝试将训练时段固定在上午，提高节律一致性。",
                "如需提升情绪调节效果，可与《开放觉察》课程交替安排。"
            )
        ),
        MbctReport(
            id = "report_20260427_02",
            title = "情绪调节专项训练报告",
            createdAt = "2026-04-27 20:15",
            courseTitle = "情绪标记",
            participantName = "张语彤",
            sessionCode = "VRM-20260427-2015",
            summary = "本次课程更偏向情绪识别与接纳。训练中后段的脑波稳定性显著提高，放松评分高于前次，适合作为夜间情绪缓冲训练。",
            completionLabel = "完成度 100%",
            riskLevel = "中低风险",
            moodBefore = 78,
            moodAfter = 55,
            focusScore = 76,
            relaxScore = 88,
            brainwaveStabilityScore = 82,
            stimulationMode = "tTCS 正常模式",
            stimulationMinutes = 30,
            currentLevel = "1.0mA",
            frequencyLabel = "0Hz",
            deviceName = "NE-200A2408403",
            deviceMacMasked = "D4:AD:20:**:**:60",
            brainwaveObservation = "课程前半程波形波动较大，提示初始唤醒偏高；随着训练推进，中后程主波形趋于平滑，后置引导期间恢复速度较快。",
            keywords = listOf("情绪识别", "晚间训练", "放松恢复"),
            timeline = listOf(
                "20:15 设备连接状态确认",
                "20:16 前置引导开始",
                "20:19 进入《情绪标记》课程",
                "20:49 刺激课程结束",
                "20:52 后置引导结束并写入记录"
            ),
            suggestions = listOf(
                "建议夜间训练后减少额外屏幕刺激。",
                "可搭配《安全空间》课程，增强情绪稳定体验。",
                "若连续 3 次训练前焦虑评分偏高，建议增加前置准备时间。"
            )
        ),
        MbctReport(
            id = "report_20260426_03",
            title = "认知负荷缓释训练报告",
            createdAt = "2026-04-26 14:05",
            courseTitle = "念头观察",
            participantName = "王泽远",
            sessionCode = "VRM-20260426-1405",
            summary = "训练以减轻反复思虑和提升认知脱钩能力为主。刺激中段后专注指标稳步上升，结束时主观疲劳感明显下降。",
            completionLabel = "完成度 96%",
            riskLevel = "低风险",
            moodBefore = 69,
            moodAfter = 50,
            focusScore = 87,
            relaxScore = 74,
            brainwaveStabilityScore = 77,
            stimulationMode = "tTCS 正常模式",
            stimulationMinutes = 30,
            currentLevel = "1.3mA",
            frequencyLabel = "0Hz",
            deviceName = "NE-200A2408403",
            deviceMacMasked = "D4:AD:20:**:**:60",
            brainwaveObservation = "课程前期存在高频短周期振荡，中段后波形趋于规律，提示受试者逐渐进入稳定观察状态，后段稳定维持时间较长。",
            keywords = listOf("认知脱钩", "疲劳缓解", "白天训练"),
            timeline = listOf(
                "14:05 创建训练会话",
                "14:06 前置引导与采集开始",
                "14:09 课程开始并启动正常模式刺激",
                "14:39 课程刺激结束",
                "14:42 完成后置引导"
            ),
            suggestions = listOf(
                "建议在高认知工作前后固定加入该课程。",
                "可与《三分钟呼吸空间》组合使用，形成短时恢复方案。",
                "若下午疲劳明显，可优先安排 14:00-16:00 时段训练。"
            )
        ),
        MbctReport(
            id = "report_20260424_04",
            title = "睡前放松辅助训练报告",
            createdAt = "2026-04-24 22:10",
            courseTitle = "睡前安定",
            participantName = "陈书宁",
            sessionCode = "VRM-20260424-2210",
            summary = "本次课程用于睡前低唤醒训练，后段脑波收敛明显，放松评分提升幅度较大，适合作为连续睡眠管理训练的一部分。",
            completionLabel = "完成度 100%",
            riskLevel = "中低风险",
            moodBefore = 74,
            moodAfter = 48,
            focusScore = 71,
            relaxScore = 92,
            brainwaveStabilityScore = 85,
            stimulationMode = "tTCS 正常模式",
            stimulationMinutes = 30,
            currentLevel = "0.9mA",
            frequencyLabel = "0Hz",
            deviceName = "NE-200A2408403",
            deviceMacMasked = "D4:AD:20:**:**:60",
            brainwaveObservation = "全程脑波波形由不稳定向平稳过渡，后置引导阶段基线收敛较好，显示受试者对放松主题课程响应积极。",
            keywords = listOf("睡眠准备", "低唤醒", "夜间恢复"),
            timeline = listOf(
                "22:10 连接设备并读取状态",
                "22:11 前置引导开始",
                "22:14 进入《睡前安定》课程",
                "22:44 课程刺激结束",
                "22:47 会话结束"
            ),
            suggestions = listOf(
                "建议在训练后 30 分钟内避免剧烈活动。",
                "后续可连续 7 天固定在同一时段训练。",
                "如存在入睡困难，可叠加《感恩安住》课程交替使用。"
            )
        ),
        MbctReport(
            id = "report_20260422_05",
            title = "开放监测联合干预报告",
            createdAt = "2026-04-22 16:30",
            courseTitle = "开放监测",
            participantName = "赵一航",
            sessionCode = "VRM-20260422-1630",
            summary = "开放监测课程下，受试者在中后程对外界刺激的卷入度下降，课程结束后自评身心负荷减轻，适合作为连续干预中的进阶课程。",
            completionLabel = "完成度 93%",
            riskLevel = "中风险",
            moodBefore = 81,
            moodAfter = 61,
            focusScore = 79,
            relaxScore = 73,
            brainwaveStabilityScore = 74,
            stimulationMode = "tTCS 正常模式",
            stimulationMinutes = 30,
            currentLevel = "1.1mA",
            frequencyLabel = "0Hz",
            deviceName = "NE-200A2408403",
            deviceMacMasked = "D4:AD:20:**:**:60",
            brainwaveObservation = "课程开始阶段脑波变化较敏感，中段后高振幅波动下降，提示开放监测训练对外界干扰抑制具有一定帮助。",
            keywords = listOf("开放监测", "干扰抑制", "进阶课程"),
            timeline = listOf(
                "16:30 设备连接完成",
                "16:31 前置引导开始",
                "16:34 进入《开放监测》课程",
                "17:04 刺激结束",
                "17:07 数据保存完成"
            ),
            suggestions = listOf(
                "建议在熟悉基础课程后再持续使用该课程。",
                "如训练中走神频繁，可先回退到《呼吸觉察》巩固一周。",
                "下一阶段可观察连续训练后的稳定性提升趋势。"
            )
        ),
        MbctReport(
            id = "report_20260420_06",
            title = "自我关怀疗程跟踪报告",
            createdAt = "2026-04-20 11:20",
            courseTitle = "自我关怀",
            participantName = "许清妍",
            sessionCode = "VRM-20260420-1120",
            summary = "训练关注自责情境下的情绪稳定与躯体放松。整体流程完成度高，刺激后情绪压迫感下降，建议继续作为周计划固定课程。",
            completionLabel = "完成度 100%",
            riskLevel = "低风险",
            moodBefore = 76,
            moodAfter = 49,
            focusScore = 80,
            relaxScore = 86,
            brainwaveStabilityScore = 83,
            stimulationMode = "tTCS 正常模式",
            stimulationMinutes = 30,
            currentLevel = "1.0mA",
            frequencyLabel = "0Hz",
            deviceName = "NE-200A2408403",
            deviceMacMasked = "D4:AD:20:**:**:60",
            brainwaveObservation = "引导阶段波形起伏偏大，课程后半段逐步平稳，提示受试者在接纳主题下更容易进入稳定状态。",
            keywords = listOf("自我接纳", "躯体放松", "周计划"),
            timeline = listOf(
                "11:20 会话创建",
                "11:21 前置引导开始",
                "11:24 进入《自我关怀》课程",
                "11:54 正常模式刺激结束",
                "11:57 完成后置引导并归档"
            ),
            suggestions = listOf(
                "建议保持每周固定 2-3 次训练频率。",
                "可在训练结束后补充简短情绪记录，便于长期跟踪。",
                "若后续希望提升专注，可与《静坐稳定》交替安排。"
            )
        )
    )

    fun findById(id: String): MbctReport? {
        return reports.find { it.id == id }
    }
}
