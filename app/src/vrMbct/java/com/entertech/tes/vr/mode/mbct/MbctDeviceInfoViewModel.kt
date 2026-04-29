package com.entertech.tes.vr.mode.mbct

import androidx.lifecycle.viewModelScope
import com.entertech.tes.vr.BaseTesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MbctDeviceInfoViewModel : BaseTesViewModel() {

    data class DeviceInfoUiState(
        val deviceName: String = "未连接",
        val deviceMac: String = "未连接",
        val connectStatusText: String = "设备连接状态：未连接",
        val hintText: String = "连接设备后可在此查看设备名称、MAC 地址以及实时设备状态。"
    )

    private val _uiState = MutableStateFlow(DeviceInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun refreshDeviceInfo() {
        val device = tesDeviceManager?.getDevice()
        val isConnected = tesDeviceManager?.isConnected() == true
        val name = device?.getName().orEmpty().ifBlank { "未连接" }
        val mac = device?.getMacAddress().orEmpty().ifBlank { "未连接" }
        _uiState.value = _uiState.value.copy(
            deviceName = name,
            deviceMac = mac,
            connectStatusText = if (isConnected) {
                "设备连接状态：已连接"
            } else {
                "设备连接状态：未连接"
            }
        )
        if (isConnected) {
            tesDeviceManager?.shakeHands(viewModelScope)
        }
    }

    override fun deviceConnected() {
        super.deviceConnected()
        refreshDeviceInfo()
    }

    override fun deviceDisconnect() {
        super.deviceDisconnect()
        refreshDeviceInfo()
    }
}
