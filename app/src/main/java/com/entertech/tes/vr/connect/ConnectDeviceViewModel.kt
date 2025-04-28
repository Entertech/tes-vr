package com.entertech.tes.vr.connect

import androidx.lifecycle.viewModelScope
import com.entertech.tes.vr.BaseTesViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ConnectDeviceViewModel : BaseTesViewModel() {
    private val _connectedStatus = MutableSharedFlow<Unit>()
    val connectedStatus = _connectedStatus.asSharedFlow()


    override fun deviceConnected() {
        super.deviceConnected()
        tesDeviceManager?.shakeHands(viewModelScope = viewModelScope)
        viewModelScope.launch {
            _connectedStatus.emit(Unit)
        }
    }

    fun isDeviceConnected(): Boolean {
        val isDeviceConnected = tesDeviceManager?.isConnected() == true
        if (isDeviceConnected) {
            tesDeviceManager?.shakeHands(viewModelScope = viewModelScope)
        }
        return isDeviceConnected
    }
}