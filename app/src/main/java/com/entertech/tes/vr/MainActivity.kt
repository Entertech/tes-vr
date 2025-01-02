package com.entertech.tes.vr

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.entertech.base.BaseActivity
import cn.entertech.base.util.startActivity

class MainActivity : BaseActivity() {
    companion object {
        const val SERVICE_UUID = "serviceUuid"
        const val DEVICE_TO_PHONE_UUID = "deviceToPhoneUUid"
        const val PHONE_TO_DEVICE_UUID = "phoneToDeviceUUid"
    }

    private var spinnerServiceUuid: Spinner? = null
    private var spinnerDeviceToPhoneUUid: Spinner? = null
    private var spinnerPhoneToDeviceUUid: Spinner? = null
    private var btnOK: Button? = null
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
        btnOK?.setOnClickListener(this)
        spinnerServiceUuid = findViewById(R.id.spinnerServiceUuid)
        spinnerDeviceToPhoneUUid = findViewById(R.id.spinnerDeviceToPhoneUUid)
        spinnerPhoneToDeviceUUid = findViewById(R.id.spinnerPhoneToDeviceUUid)
        initSpinner(spinnerServiceUuid, serviceUUidList) {
            serviceUuid = it
        }
        initSpinner(spinnerDeviceToPhoneUUid, deviceToPhoneUUidList) {
            deviceToPhoneUUid = it
        }
        initSpinner(spinnerPhoneToDeviceUUid, phoneToDeviceUUidList) {
            phoneToDeviceUUid = it
        }
    }

    private fun initSpinner(spinner: Spinner?, data: List<String>, select: (String) -> Unit) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item_layout, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
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
                val bundle = Bundle()
                bundle.putString(SERVICE_UUID, serviceUuid)
                bundle.putString(DEVICE_TO_PHONE_UUID, deviceToPhoneUUid)
                bundle.putString(PHONE_TO_DEVICE_UUID, phoneToDeviceUUid)
                startActivity(ControlDeviceActivity::class.java, bundle, false)
            }
        }
    }
}