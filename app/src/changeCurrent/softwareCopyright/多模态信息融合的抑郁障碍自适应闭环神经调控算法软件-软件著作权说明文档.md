# 《多模态信息融合的抑郁障碍自适应闭环神经调控算法软件》
# 软件著作权说明文档

**软件名称**：多模态信息融合的抑郁障碍自适应闭环神经调控算法软件

**文档目的**：从软件实现角度说明本软件的研发背景、系统总体架构、主要功能模块、核心算法与运行机制，并给出关键算法的 Kotlin 代码示例。

## 1. 研发背景
抑郁障碍的治疗需要兼顾个体差异与实时状态变化。传统固定参数刺激难以适配不同人群、不同阶段的神经生理波动。本软件以多模态生理信号为输入，通过实时融合与评估构建闭环控制模型，实现对刺激电流的动态调整，提供可解释、可跟踪、可扩展的神经调控算法框架。

## 2. 系统总体架构
系统采用分层与模块化架构，核心路径为“数据采集 → 数据融合 → 状态评估 → 决策控制 → 设备执行 → 反馈更新”的闭环。

架构组成：
1. 数据采集层：采集 EEG、心率、皮电、运动状态等多模态生理信号与设备状态。
2. 数据融合层：对多源时序数据进行对齐、归一化、特征提取与融合。
3. 状态评估层：输出情绪/状态相关指标（如紧张度、负性趋势指数）。
4. 决策控制层：依据评估结果产出电流调整策略（增加/减少/保持）。
5. 设备控制层：通过设备 SDK 调用电流调节与模式启动接口。
6. 交互与监控层：提供运行状态、设备信息、消息日志等可视化反馈。

## 3. 主要功能模块
1. 设备连接与生命周期管理模块：负责设备连接、状态监听与生命周期回收。
2. 多模态生理信号融合模块：完成多源信号的预处理、特征构建与融合。
3. 自适应电流调节模块：依据融合结果进行电流动态调整。
4. 闭环控制与调度模块：按固定周期执行“评估→决策→执行→反馈”。
5. 运行监控与日志模块：统一日志与状态输出，便于调试与追踪。

## 4. 多模态生理信号数据融合算法
多模态融合流程包含“时间对齐、异常剔除、特征提取、融合建模”四步。融合结果以标准化指标供决策层使用。算法实现支持在线处理，并可根据实际传感器扩展特征集合。

算法描述（实现视角）：
1. 以统一时间戳对多源信号进行对齐与重采样。
2. 对各模态做去噪、去伪迹与异常检测。
3. 提取时域与频域特征，如均值、方差、功率谱密度等。
4. 对特征向量进行归一化，并基于权重或学习模型融合为状态向量。

多模态融合示例代码（Kotlin）：
```kotlin
// 多模态融合示例：将多个模态特征汇总为统一状态向量
// 该代码体现在线处理结构，具体特征与权重可按实际传感器配置扩展

data class FeatureVector(
    val eeg: List<Double>,
    val hrv: List<Double>,
    val eda: List<Double>,
    val motion: List<Double>
)

data class FusedState(
    val arousalIndex: Double,
    val stressIndex: Double,
    val trendIndex: Double
)

class MultimodalFusionEngine(
    private val weightEeg: Double,
    private val weightHrv: Double,
    private val weightEda: Double,
    private val weightMotion: Double
) {
    fun fuse(features: FeatureVector): FusedState {
        val eegScore = features.eeg.average()
        val hrvScore = features.hrv.average()
        val edaScore = features.eda.average()
        val motionScore = features.motion.average()

        val arousal = (weightEeg * eegScore + weightEda * edaScore) / (weightEeg + weightEda)
        val stress = (weightHrv * hrvScore + weightEda * edaScore) / (weightHrv + weightEda)
        val trend = (arousal + stress - motionScore * weightMotion).coerceIn(0.0, 1.0)

        return FusedState(
            arousalIndex = arousal,
            stressIndex = stress,
            trendIndex = trend
        )
    }
}
```

## 5. 自适应电流调节算法
自适应电流调节以融合状态为输入，输出三种决策结果：Increase、Decrease、Keep。该接口设计支持外部算法模块替换与扩展，控制层仅关心结果语义。

接口定义（与项目代码一致）：
```kotlin
interface CurrentAdjustAlgorithm {
    fun calculateAdjustment(): AdjustmentResult
}

sealed class AdjustmentResult {
    object Increase : AdjustmentResult()
    object Decrease : AdjustmentResult()
    object Keep : AdjustmentResult()
}
```

动态调节核心逻辑（与项目实现一致）：
```kotlin
// ChangeModeViewModel 中的调度与决策执行逻辑
private const val ADJUST_INTERVAL_MS = 600L
private val adjustRunnable: Runnable by lazy { Runnable { performAdjustment() } }

private fun performAdjustment() {
    val algorithm = currentAdjustAlgorithm ?: return
    when (algorithm.calculateAdjustment()) {
        AdjustmentResult.Increase -> {
            tesDeviceManager?.increaseCurrent(0x01, {
                TesVrLog.d(TAG, "增加电流成功")
            }) {
                TesVrLog.d(TAG, "增加电流失败 $it")
            }
        }
        AdjustmentResult.Decrease -> {
            tesDeviceManager?.reduceCurrent(0x02)
        }
        AdjustmentResult.Keep -> {
            // no-op
        }
    }
    mainHandler.postDelayed(adjustRunnable, ADJUST_INTERVAL_MS)
}
```

## 6. 闭环神经调控流程
闭环流程从系统启动到终止遵循以下步骤：
1. 启动设备并进入刺激模式（设置模式、时间与频率参数）。
2. 启动周期性调度器，在固定时间间隔内运行算法。
3. 获取多模态数据并进行融合，计算当前状态指标。
4. 调用自适应算法输出调整结果。
5. 通过设备 SDK 执行电流调整，等待设备反馈。
6. 记录状态与日志并进入下一轮循环。

启动设备与闭环启动示例代码（与项目实现一致）：
```kotlin
// ChangeModeViewModel 中的设备启动逻辑
fun startChangeMode(time: Int = 30) {
    TesVrLog.d(TAG, "开启设备-Change Mode")
    setArgAndStart(ModeType.TDCS_P.des, time = time, frequency = 0)
}
```

## 7. 软件运行机制
软件运行机制以“状态驱动 + 协程调度 + 设备回调”构成：
1. Activity 负责启动模式、监听 ViewModel 的状态流，并刷新 UI。
2. ViewModel 负责设备初始化、消息解析、算法调度与电流控制。
3. 设备反馈通过监听回调进入统一消息处理入口，确保状态一致性。
4. 关键日志通过 TesVrLog 统一输出，便于运行追踪与问题定位。

运行机制中的状态监听示例（与项目实现一致）：
```kotlin
// Activity 监听设备状态与消息
lifecycleScope.launch(Dispatchers.Main) {
    viewModel.deviceInfo.collect {
        if (it.isNotEmpty()) {
            tvDeviceInfo?.text = it
        }
    }
}

lifecycleScope.launch(Dispatchers.Main) {
    viewModel.receiveMsg.collect {
        if (it.isNotEmpty()) {
            tvReceiveMsg?.text = it
        }
    }
}
```

## 8. 关键实现特性总结
1. 模块化算法接口：动态调节算法通过接口注入，便于更换与升级。
2. 闭环可控与可追踪：周期调度与设备回调结合，保证反馈闭环稳定。
3. 兼容多模态扩展：融合层可扩展至更多生理传感器与特征集合。
4. 工程可维护性：Activity 与 ViewModel 职责清晰，符合现有项目架构。

