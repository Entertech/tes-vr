package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R
import com.entertech.tes.vr.control.log.FileListActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MbctDataCenterActivity : AppCompatActivity() {

    companion object {
        private val fileTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    }

    private var tvDataSummary: TextView? = null
    private var tvFileList: TextView? = null
    private var btnRefreshData: Button? = null
    private var btnOpenReportList: Button? = null
    private var btnOpenFileViewer: Button? = null
    private var btnBackHome: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_data_center)
        tvDataSummary = findViewById(R.id.tvDataSummary)
        tvFileList = findViewById(R.id.tvFileList)
        btnRefreshData = findViewById(R.id.btnRefreshData)
        btnOpenReportList = findViewById(R.id.btnOpenReportList)
        btnOpenFileViewer = findViewById(R.id.btnOpenFileViewer)
        btnBackHome = findViewById(R.id.btnBackHome)

        btnRefreshData?.setOnClickListener {
            renderData()
        }
        btnOpenReportList?.setOnClickListener {
            startActivity(Intent(this, MbctReportListActivity::class.java))
        }
        btnOpenFileViewer?.setOnClickListener {
            startActivity(Intent(this, FileListActivity::class.java))
        }
        btnBackHome?.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        renderData()
    }

    private fun renderData() {
        val rootDir = MbctRecordStore.getRootDir(this)
        val files = MbctRecordStore.listSessionFiles(this)
        val latest = files.firstOrNull()
        tvDataSummary?.text = buildString {
            append("本地目录：")
            append(rootDir.absolutePath)
            append("\n记录文件数：")
            append(files.size)
            append("\n课程库数量：")
            append(MbctCourseCatalog.courses.size)
            append("\n最新文件：")
            append(latest?.name ?: "暂无")
            append("\nMock 报表数：")
            append(MbctReportMocks.reports.size)
        }
        tvFileList?.text = if (files.isEmpty()) {
            "当前尚无会话记录。完成一次训练后，这里会展示最近生成的 JSONL 数据文件。"
        } else {
            buildString {
                files.take(8).forEachIndexed { index, file ->
                    append(index + 1)
                    append(". ")
                    append(file.name)
                    append("\n   ")
                    append(fileTimeFormat.format(Date(file.lastModified())))
                    append("  |  ")
                    append(file.length())
                    append(" bytes")
                    if (index != files.take(8).lastIndex) {
                        append("\n\n")
                    }
                }
            }
        }
    }
}
