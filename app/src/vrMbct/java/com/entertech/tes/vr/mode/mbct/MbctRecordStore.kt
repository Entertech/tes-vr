package com.entertech.tes.vr.mode.mbct

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MbctRecordStore {
    private const val DIR_NAME = "vr_mbct"
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val sessionIdFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun createSessionId(): String {
        return sessionIdFormat.format(Date())
    }

    fun getSessionFile(context: Context, sessionId: String): File {
        val rootDir = File(context.getExternalFilesDir(null), DIR_NAME)
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        return File(rootDir, "mbct_session_$sessionId.jsonl")
    }

    fun appendRecord(
        context: Context,
        sessionId: String,
        stage: String,
        payload: Map<String, Any?> = emptyMap()
    ): File {
        val record = JSONObject().apply {
            put("sessionId", sessionId)
            put("stage", stage)
            put("timestamp", timestampFormat.format(Date()))
            payload.forEach { (key, value) ->
                put(key, value ?: JSONObject.NULL)
            }
        }
        val sessionFile = getSessionFile(context, sessionId)
        sessionFile.appendText(record.toString() + "\n")
        return sessionFile
    }
}
