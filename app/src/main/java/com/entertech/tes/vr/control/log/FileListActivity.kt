package com.entertech.tes.vr.control.log

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.android.base.view.ToastUtil
import cn.entertech.base.BaseActivity
import cn.entertech.base.list.adapter.BaseRecyclerViewAdapter
import cn.entertech.base.list.adapter.IRecycleViewClickListener
import cn.entertech.base.util.startActivity
import cn.entertech.ble.log.BleLogUtil
import com.entertech.tes.vr.R
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FileListActivity : BaseActivity(), IRecycleViewClickListener<File> {

    companion object {
        private const val TAG = "FileListActivity"
        const val FILE_PATH = "filePath"
    }

    private val mLogListAdapter by lazy {
        FileListAdapter(clickListener = this)
    }
    private var rvLogFileList: RecyclerView? = null

    override fun getActivityLayoutResId(): Int {
        return R.layout.log_list_activity
    }

    override fun initActivityData() {
        super.initActivityData()
        val filePath = intent.getStringExtra(FILE_PATH)
        BleLogUtil.d(TAG, "filePath $filePath")
        val rootFile = if (filePath.isNullOrEmpty()) {
            application.getExternalFilesDir(null)
        } else {
            File((filePath))
        }
        val fileList = rootFile?.listFiles()?.toList() ?: emptyList()
        if (fileList.isEmpty()) {
            finish()
            return
        }
        mLogListAdapter.setData(fileList)
    }

    override fun initActivityView() {
        super.initActivityView()
        rvLogFileList = findViewById(R.id.rvLogFileList)
        rvLogFileList?.adapter = mLogListAdapter
        rvLogFileList?.layoutManager = LinearLayoutManager(this)

    }

    override fun itemClick(
        adapter: BaseRecyclerViewAdapter<File, *>, view: View?, position: Int, target: File
    ) {
        if (target.isDirectory) {
            val bundle = Bundle()
            bundle.putString(FILE_PATH, target.path)
            startActivity(FileListActivity::class.java, bundle, finishCurrent = false)
        } else {
            openTextFile(target)
        }
    }

    override fun itemLongClick(
        adapter: BaseRecyclerViewAdapter<File, *>, view: View?, position: Int, target: File
    ) {
        if (!target.isDirectory) {
            shareFile(target)
        } else {
            //压缩为zip，分享zip文件
            val zipFilePath =
                "${applicationContext.getExternalFilesDir("zipCache")}/${target.name}.zip"
            try {
                zipFolder(target, zipFilePath)
                shareZipFile(zipFilePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun shareZipFile(zipFilePath: String) {
        val zipFile = File(zipFilePath)
        if (!zipFile.exists()) {
            throw IllegalArgumentException("The zip file does not exist.")
        }

        val zipUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", zipFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, zipUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share ZIP File"))
    }

    private fun zipFolder(folder: File, zipFilePath: String) {
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("The folder path is invalid or not a directory.")
        }

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFilePath))).use { zipOut ->
            zipFolderContents(folder, folder.name, zipOut)
        }
    }

    private fun zipFolderContents(folder: File, parentPath: String, zipOut: ZipOutputStream) {
        folder.listFiles()?.forEach { file ->
            val zipEntryName = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
            if (file.isDirectory) {
                zipFolderContents(file, zipEntryName, zipOut)
            } else {
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(zipEntryName)
                    zipOut.putNextEntry(zipEntry)
                    fis.copyTo(zipOut)
                    zipOut.closeEntry()
                }
            }
        }
    }

    private fun openTextFile(file: File) {
        if (file.exists()) {
            try {
                val fileUri: Uri = FileProvider.getUriForFile(
                    this, "${packageName}.fileprovider", file
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "text/plain")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // 处理没有应用可以打开文件的情况
                    ToastUtil.toastShort(this, "没有应用可以打开此文件")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            ToastUtil.toastShort(this, "$file 文件不存在")
        }
    }

    private fun shareFile(file: File) {
        if (file.exists()) {
            val fileUri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // 根据文件类型设置 MIME 类型
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // 启动分享文件的选择器
            startActivity(Intent.createChooser(intent, "Share file using"))
        } else {
            ToastUtil.toastShort(this, "文件不存在")
        }
    }
}