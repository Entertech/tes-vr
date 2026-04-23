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

    fun getRootDir(context: Context): File {
        val rootDir = File(context.getExternalFilesDir(null), DIR_NAME)
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        return rootDir
    }

    fun createSessionId(): String {
        return sessionIdFormat.format(Date())
    }

    fun getSessionFile(context: Context, sessionId: String): File {
        val rootDir = getRootDir(context)
        return File(rootDir, "mbct_session_$sessionId.jsonl")
    }

    fun listSessionFiles(context: Context): List<File> {
        return getRootDir(context)
            .listFiles()
            ?.filter { it.isFile && it.name.startsWith("mbct_session_") && it.extension == "jsonl" }
            ?.sortedByDescending { it.lastModified() }
            .orEmpty()
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
