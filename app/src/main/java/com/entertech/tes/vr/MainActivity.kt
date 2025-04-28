package com.entertech.tes.vr

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.entertech.base.util.startActivity
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.TesDeviceManager.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.ble.device.TesDeviceManager.Companion.PHONE_TO_DEVICE_UUID
import com.entertech.tes.vr.connect.ConnectDeviceActivity
import com.entertech.tes.vr.control.ControlDeviceActivity
import com.entertech.tes.vr.control.log.FileListActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val SERVICE_UUID = "serviceUuid"
        private const val TAG = "MainActivity"

    }

    private var spinnerServiceUuid: Spinner? = null
    private var spinnerDeviceToPhoneUUid: Spinner? = null
    private var spinnerPhoneToDeviceUUid: Spinner? = null
    private var btnOK: Button? = null
    private var btnDebug: Button? = null
    private var serviceUuid: String = ""
    private var deviceToPhoneUUid: String = ""
    private var phoneToDeviceUUid: String = ""

    private val serviceUUidList by lazy {
        listOf(
            "0003cdd0-0000-1000-8000-00805f9b0131"
        )
    }

    private val deviceToPhoneUUidList by lazy {
        listOf(
            "0003cdd1-0000-1000-8000-00805f9b0131"
        )
    }

    private val phoneToDeviceUUidList by lazy {
        listOf(
            "0003cdd2-0000-1000-8000-00805f9b0131"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOK = findViewById(R.id.btnOK)
        btnDebug = findViewById(R.id.btnDebug)
        btnOK?.setOnClickListener(this)
        btnDebug?.setOnClickListener(this)
        spinnerServiceUuid = findViewById(R.id.spinnerServiceUuid)
        spinnerDeviceToPhoneUUid = findViewById(R.id.spinnerDeviceToPhoneUUid)
        spinnerPhoneToDeviceUUid = findViewById(R.id.spinnerPhoneToDeviceUUid)
        findViewById<View>(R.id.showData)?.setOnClickListener(this)
        initSpinner(spinnerServiceUuid, serviceUUidList) {
            serviceUuid = it
        }
        initSpinner(spinnerDeviceToPhoneUUid, deviceToPhoneUUidList) {
            deviceToPhoneUUid = it
        }
        initSpinner(spinnerPhoneToDeviceUUid, phoneToDeviceUUidList) {
            phoneToDeviceUUid = it
        }
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    (this as Activity), arrayOf(POST_NOTIFICATIONS), 0
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TesVrLog.d(TAG, "通知权限已授予")
            } else {
                TesVrLog.e(TAG, "用户拒绝了通知权限")
            }
        }
    }

    private fun initSpinner(spinner: Spinner?, data: List<String>, select: (String) -> Unit) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item_layout, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // 获取选择的项
                select(data[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 没有选择任何项
                spinner?.setSelection(0)
                select(data[0])
            }
        }
        spinner?.setSelection(0)
        select(data[0])
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnOK -> {
                val intent = Intent(this, ConnectDeviceActivity::class.java)
                intent.putExtra(SERVICE_UUID, serviceUuid)
                intent.putExtra(DEVICE_TO_PHONE_UUID, deviceToPhoneUUid)
                intent.putExtra(PHONE_TO_DEVICE_UUID, phoneToDeviceUUid)
                startActivity(intent)
            }

            R.id.btnDebug -> {
                val intent = Intent(this, ControlDeviceActivity::class.java)
                intent.putExtra(SERVICE_UUID, serviceUuid)
                intent.putExtra(DEVICE_TO_PHONE_UUID, deviceToPhoneUUid)
                intent.putExtra(PHONE_TO_DEVICE_UUID, phoneToDeviceUUid)
                startActivity(intent)
            }

            R.id.showData -> {
                startActivity(FileListActivity::class.java, finishCurrent = false)
            }
        }
    }
}