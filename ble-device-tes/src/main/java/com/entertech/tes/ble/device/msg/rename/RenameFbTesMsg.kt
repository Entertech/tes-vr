package com.entertech.tes.ble.device.msg.rename

import com.entertech.tes.ble.device.msg.BaseReceiveTesMsg

class RenameFbTesMsg: BaseReceiveTesMsg(),IRenameFunction{
    companion object{
        const val INDEX_RENAME_RESULT = 0
        const val RENAME_RESULT_SUCCESS: Byte = 0x00
        const val RENAME_RESULT_FAILURE: Byte = 0x01
    }

    override fun processMsgData(byteArray: ByteArray): Boolean {
        val result = byteArray.getOrNull(INDEX_DATA_START + INDEX_RENAME_RESULT)
        when (result) {
            RENAME_RESULT_SUCCESS -> {
            }

            RENAME_RESULT_FAILURE -> {
            }
        }
        return true
    }

    override fun getMsgLength(): Byte {
        return 0x07
    }

}