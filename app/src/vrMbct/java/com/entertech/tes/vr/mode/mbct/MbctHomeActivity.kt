package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.MainActivity
import com.entertech.tes.vr.R
import com.entertech.tes.ble.device.TesDeviceManager.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.ble.device.TesDeviceManager.Companion.PHONE_TO_DEVICE_UUID
import com.entertech.tes.vr.connect.ConnectDeviceActivity
import com.entertech.tes.vr.mode.ChooseModeActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MbctHomeActivity : AppCompatActivity() {

    companion object {
        private const val DEFAULT_SERVICE_UUID = "0003cdd0-0000-1000-8000-00805f9b0131"
        private const val DEFAULT_DEVICE_TO_PHONE_UUID = "0003cdd1-0000-1000-8000-00805f9b0131"
        private const val DEFAULT_PHONE_TO_DEVICE_UUID = "0003cdd2-0000-1000-8000-00805f9b0131"
        private val dayFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    }

    private var tvWelcomeTitle: TextView? = null
    private var tvWelcomeSubtitle: TextView? = null
    private var tvAccountSummary: TextView? = null
    private var tvCourseCount: TextView? = null
    private var tvRecordCount: TextView? = null
    private var tvLatestRecord: TextView? = null
    private var btnConnectAndTrain: Button? = null
    private var btnOpenModeCenter: Button? = null
    private var btnOpenDataCenter: Button? = null
    private var btnOpenAccount: Button? = null
    private var btnOpenDeviceInfo: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MbctUserStore.isLoggedIn(this)) {
            startActivity(Intent(this, MbctLoginActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_mbct_home)
        tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle)
        tvWelcomeSubtitle = findViewById(R.id.tvWelcomeSubtitle)
        tvAccountSummary = findViewById(R.id.tvAccountSummary)
        tvCourseCount = findViewById(R.id.tvCourseCount)
        tvRecordCount = findViewById(R.id.tvRecordCount)
        tvLatestRecord = findViewById(R.id.tvLatestRecord)
        btnConnectAndTrain = findViewById(R.id.btnConnectAndTrain)
        btnOpenModeCenter = findViewById(R.id.btnOpenModeCenter)
        btnOpenDataCenter = findViewById(R.id.btnOpenDataCenter)
        btnOpenAccount = findViewById(R.id.btnOpenAccount)
        btnOpenDeviceInfo = findViewById(R.id.btnOpenDeviceInfo)

        btnConnectAndTrain?.setOnClickListener {
            startActivity(createConnectIntent())
        }
        btnOpenModeCenter?.setOnClickListener {
            startActivity(Intent(this, ChooseModeActivity::class.java))
        }
        btnOpenDataCenter?.setOnClickListener {
            startActivity(Intent(this, MbctDataCenterActivity::class.java))
        }
        btnOpenAccount?.setOnClickListener {
            startActivity(Intent(this, MbctAccountActivity::class.java))
        }
        btnOpenDeviceInfo?.setOnClickListener {
            startActivity(Intent(this, MbctDeviceInfoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        renderDashboard()
    }

    private fun renderDashboard() {
        val user = MbctUserStore.getUser(this)
        val files = MbctRecordStore.listSessionFiles(this)
        tvWelcomeTitle?.text = "${user?.fullName ?: "访客"}，欢迎回来"
        tvWelcomeSubtitle?.text =
            "今天是 ${dayFormat.format(Date())}，建议先完成设备连接，再开始一次 30min VR-MBCT 课程。"
        tvAccountSummary?.text =
            "当前账号：${user?.username.orEmpty()}  |  所属机构：${user?.organization.orEmpty()}"
        tvCourseCount?.text = MbctCourseCatalog.courses.size.toString()
        tvRecordCount?.text = files.size.toString()
        tvLatestRecord?.text = if (files.isEmpty()) {
            "最近记录：暂无本地训练数据，可从主页直接进入连接和训练流程。"
        } else {
            val latest = files.first()
            "最近记录：${latest.name}\n更新时间：${dayFormat.format(Date(latest.lastModified()))}"
        }
    }

    private fun createConnectIntent(): Intent {
        return Intent(this, ConnectDeviceActivity::class.java).apply {
            putExtra(MainActivity.SERVICE_UUID, DEFAULT_SERVICE_UUID)
            putExtra(DEVICE_TO_PHONE_UUID, DEFAULT_DEVICE_TO_PHONE_UUID)
            putExtra(PHONE_TO_DEVICE_UUID, DEFAULT_PHONE_TO_DEVICE_UUID)
        }
    }
}
