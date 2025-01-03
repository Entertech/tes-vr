package com.entertech.tes.ble.device

enum class DeviceStatus(val byte: Byte) {
    DEVICE_STATUS_RUNNING(0x01),
    DEVICE_STATUS_READY(0x02),
    DEVICE_STATUS_ERROR(0x03);

    companion object {
        private val map by lazy {
            val map = HashMap<Byte, DeviceStatus>()
            entries.forEach {
                map[it.byte] = it
            }
            map
        }

        fun getStatus(byte: Byte): DeviceStatus? {
            return map[byte]
        }
    }
}