# TesDeviceManager SDK 文档

## 简介

`TesDeviceManager` 是一个基于 BLE（蓝牙低功耗）的设备管理类，用于与特定的蓝牙设备进行连接、通信和控制。\
它提供了设备连接、消息发送与接收、电流调节、伪刺激模式控制等功能。

***

## 目录

*   [集成](#集成)
*   [类概述](#类概述)
*   [构造函数](#构造函数)
*   [主要方法](#主要方法)
  *   [设备连接相关](#设备连接相关)
  *   [消息发送与接收](#消息发送与接收)
  *   [设备控制](#设备控制)
  *   [电流调节](#电流调节)
  *   [伪刺激模式](#伪刺激模式)
  *   [事件监听](#事件监听)
  *   [BaseReceiveTesMsg消息类型](#BaseReceiveTesMsg消息类型)
*   [辅助方法](#辅助方法)
*   [备注](#备注)

***

## 集成

## 集成

### gradle自动依赖

在项目根目录的build.gradle文件下添加以下依赖：

```groovy
repositories {
  mavenCentral()
}
```

在所需的module中的build.gradle文件下添加以下依赖：

```groovy
implementation 'cn.entertech.android:ble-device-tes:0.0.2'
```

***

## 类概述

```kotlin
class TesDeviceManager(
    context: Context, 
    private val deviceToPhoneUUid: String, 
    private val phoneToDeviceUUid: String
) : BaseBleConnectManager(context)
```

该类继承 BaseBleConnectManager，用于管理与蓝牙设备的连接与通信。

***

## 构造函数

TesDeviceManager(context: Context, deviceToPhoneUUid: String, phoneToDeviceUUid: String)

参数

*   context: Context - Android 应用上下文。
*   deviceToPhoneUUid: String - 设备到手机的 UUID。
*   phoneToDeviceUUid: String - 手机到设备的 UUID。

***

## 主要方法

### 设备连接相关

#### 通过设备的 MAC 地址 进行连接。

    connectDeviceByMac(mac: String, success: (String) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   mac: String - 设备的 MAC 地址。
*   success: (String) -> Unit - 连接成功的回调，返回设备地址。
*   failure: (String) -> Unit - 连接失败的回调，返回错误信息。

### 消息发送与接收

#### 发送消息到设备。

    sendMessage(msg: BaseSendTesMsg, needCheckStatus: Boolean = false, success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   msg: BaseSendTesMsg - 需要发送的消息对象。
*   needCheckStatus: Boolean - 是否需要检查设备状态。
*   success: (ByteArray) -> Unit - 发送成功回调，返回消息字节数组。
*   failure: (String) -> Unit - 发送失败回调，返回错误信息。

#### receiveMessage(rawMsgListener: (ByteArray) -> Unit, failure: (String) -> Unit = {})

接收设备消息。

参数

*   rawMsgListener: (ByteArray) -> Unit - 监听设备消息的回调，返回字节数组。
*   failure: (String) -> Unit - 失败时的回调，返回错误信息。

### 设备控制

#### 启动设备。

    startDevice(mode: String, time: Int, frequency: Int, success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   mode: String - 设备模式。
*   time: Int - 运行时间（秒）。
*   frequency: Int - 运行频率。
*   success: (ByteArray) -> Unit - 成功回调。
*   failure: (String) -> Unit - 失败回调。

#### 停止设备。

    stopDevice(success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   success: (ByteArray) -> Unit - 成功回调。
*   failure: (String) -> Unit - 失败回调。

### 电流调节

#### 增加设备电流。

    increaseCurrent(step: Byte, success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数
•	step: Byte - 递增步长。
•	success: (ByteArray) -> Unit - 成功回调。
•	failure: (String) -> Unit - 失败回调。

#### 减少设备电流。

    reduceCurrent(step: Byte, success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   step: Byte - 递减步长。
*   success: (ByteArray) -> Unit - 成功回调。
*   failure: (String) -> Unit - 失败回调。

### 伪刺激模式

#### 开始伪刺激模式。

    startFakeMode(success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   success: (ByteArray) -> Unit - 成功回调。
*   failure: (String) -> Unit - 失败回调。

#### 停止伪刺激模式。

    stopFakeMode(success: (ByteArray) -> Unit = {}, failure: (String) -> Unit = {})

参数

*   success: (ByteArray) -> Unit - 成功回调。
*   failure: (String) -> Unit - 失败回调。

### 事件监听

#### 添加设备消息监听器。

    addDeviceMessageListener(listener: (BaseReceiveTesMsg?) -> Unit)

参数

*   listener: (BaseReceiveTesMsg?) -> Unit - 消息回调。

#### 移除设备消息监听器。

    removeDeviceMessageListener(listener: (BaseReceiveTesMsg?) -> Unit)

参数

*   listener: (BaseReceiveTesMsg?) -> Unit - 消息回调。


#### 添加原始数据监听器。

    addDeviceMessageRawDataListener(listener: (ByteArray) -> Unit)

参数

*   listener: (ByteArray) -> Unit - 监听回调。

#### 移除原始数据监听器。

    removeDeviceMessageRawDataListener(listener: (ByteArray) -> Unit)

参数

*   listener: (ByteArray) -> Unit - 监听回调。

***
### BaseReceiveTesMsg消息类型
#### 控制命令反馈消息 ControlCommandFdTesMsg
controlResult 控制指令结果
- CONTROL_RESULT_SUCCESS 成功
- CONTROL_RESULT_FAILURE 失败


#### 调节电流反馈消息RegulationCurrentFbTesMsg
1.regulationType：
- REGULATION_CURRENT_INCREASE 电流增加
- REGULATION_CURRENT_REDUCE 电流减小

2.regulationResult ：调节结果
-   REGULATION_RESULT_SUCCESS 调节成功；
-   REGULATION_RESULT_TIME_NOT_ENOUGH:剩余时间不足，无法调至到此模式；
-   REGULATION_RESULT_DEVICE_IN_REDUCE:设备正在缓降阶段无法 调节电流;
-   REGULATION_RESULT_FAILURE:失败

#### 设置参数并启动反馈消息SettingArgFbTesMsg
setArgResult：
* SETTING_ARG_RESULT_SUCCESS:成功
* SETTING_ARG_RESULT_FAILURE:失败
* SETTING_ARG_RESULT_ERROR:参数错误

#### 开始伪刺激反馈消息StartPseudoStimulateFbTesMsg
startSuccess：true 开始成功；false 开始失败

#### 停止伪刺激反馈消息StopPseudoStimulateFbTesMsg
stopSuccess：true 停止成功；false 停止失败

#### 事件上报反馈消息UploadTesFbMsg
1. newDeviceStatus 设备状态

DEVICE_STATUS_IN_VALID  无效
DEVICE_STATUS_RUNNING  设备运行中
DEVICE_STATUS_READY  设备就绪中
DEVICE_STATUS_ERROR  设备故障
DEVICE_STATUS_CUR_DOWNING_NORMAL  设备当前正在正常降压
DEVICE_STATUS_CUR_DOWNING_CONTROL  设备当前正在控制降压
DEVICE_STATUS_STIMULATE_PSEUDO  伪刺激

2. deviceBattery 电池电量
3. frequency 频率
4. stimulateRemainTime 剩余刺激时间
5. impedanceValue 当前阻抗值
6. currentMode 模式
- TDCS_P tDCS 正向
- tDCS_N tDCS 负向
- TACS TACS

7. setCurrent 设定的电流
8. setTime 设定的时间 单位s
9. current 当前电流
10. stopFlag 停止标识
- STOP_FLAG_NORMAL：正常周期性发送
- STOP_FLAG_STOPING:正在停止中（正常结束缓降）
- STOP_FLAG_STOP_COMPLETE:停止完成
- STOP_FLAG_STOP_FAIL：停止失败
- STOP_FLAG_STOP_RECEIVE:停止命令已经接收到，主动缓降停止
11. currentLevel 电流的阶段
12. currentLimit 电流限制


### 辅助方法

```

intToLittleEndianBytes(value: Int): ByteArray
```

将整数转换为小端字节数组。

参数

*   value: Int - 需要转换的整数。

返回

*   ByteArray - 转换后的字节数组。

***

## 备注

*   确保在调用 sendMessage、receiveMessage、startDevice 等方法之前，设备已成功连接。
*   设备状态可以通过 getDeviceStatus() 获取，并可以使用 setDeviceStatus(status) 进行设置。

