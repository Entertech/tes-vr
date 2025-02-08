package com.entertech.tes.vr

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import cn.entertech.base.BaseService
import cn.entertech.base.notification.INotificationChannelType
import cn.entertech.base.notification.NotificationUtils
import com.entertech.tes.ble.R
import com.entertech.tes.ble.TesVrLog
import com.entertech.tes.ble.device.TesDeviceManager
import com.entertech.tes.ble.device.TesDeviceManager.Companion.DEVICE_TO_PHONE_UUID
import com.entertech.tes.ble.device.TesDeviceManager.Companion.PHONE_TO_DEVICE_UUID

class TesDeviceManagerService : BaseService() {

    companion object {
        private const val TAG = "TesDeviceManagerService"
        const val SERVICE_NOTIFICATION_TITLE = "title"
        const val SERVICE_NOTIFICATION_CONTENT = "content"
        const val SERVICE_NOTIFICATION_SMALL_ICON = "smallIcon"
        const val SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION = "ChannelDescription"
        const val SERVICE_NOTIFICATION_CHANNEL_ID = "ChannelId"
        const val SERVICE_NOTIFICATION_CHANNEL_TYPE_NAME = "ChannelTypeName"
        var isRunning = false
    }

    private var macAddress: String = ""
    private val connectListener: (String) -> Unit by lazy {
        { mac ->
            mainHandler.post {
                macAddress = mac
                updateNotification("蓝牙已连接", "设备已成功连接")
            }
        }
    }
    private val disconnectListener: (String) -> Unit by lazy {
        { error ->
            TesVrLog.e(TAG, "连接设备失败：$error")
            mainHandler.post {
                updateNotification("蓝牙未连接", "设备已断开...")
            }
            mainHandler.postDelayed({
                updateNotification("蓝牙未连接", "正在尝试重新连接...")
                tesDeviceManager?.connectDeviceByMac(macAddress)
            }, 1000)
        }
    }

    class TesServiceBinder(private val service: TesDeviceManagerService) : Binder() {
        fun getTesDeviceManager() = service.getTesDeviceManager()
    }

    private var tesDeviceManager: TesDeviceManager? = null


    override fun onBind(intent: Intent?): IBinder {
        initDeviceManager(intent)
        return TesServiceBinder(this)
    }



    private fun updateNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                TesVrLog.e(TAG, "通知权限未授予，无法显示通知 title $title content $content")
                return
            }
        }
        val smallIcon = R.drawable.ic_launcher_foreground // 可更换成蓝牙相关图标
        val channelId = getString(R.string.tes_device_service_default_channel_id)
        val notification = NotificationUtils.createNotification(
            this,
            NotificationUtils.createNotificationChannel(this, object : INotificationChannelType {
                override fun getChannelDescription(): String {
                    return getString(R.string.tes_device_service_default_channel_description)
                }

                override fun getChannelId(): String {
                    return channelId
                }

                override fun getChannelImportance(): Int {
                    return NotificationManager.IMPORTANCE_HIGH
                }

                override fun getChannelTypeName(): String {
                    return getString(R.string.tes_device_service_default_channel_type_name)
                }
            }),
            title,
            content,
            smallIcon = smallIcon
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(123, notification) // 更新通知
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initDeviceManager(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 将服务设置为前台服务，并显示通知
            val title = intent?.getStringExtra(SERVICE_NOTIFICATION_TITLE)
                ?: getString(R.string.tes_device_service_default_title)
            val content = intent?.getStringExtra(SERVICE_NOTIFICATION_CONTENT)
                ?: getString(R.string.tes_device_service_default_content)
            val channelDescription =
                intent?.getStringExtra(SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION)
                    ?: getString(R.string.tes_device_service_default_channel_description)
            val channelId = intent?.getStringExtra(SERVICE_NOTIFICATION_CHANNEL_ID)
                ?: getString(R.string.tes_device_service_default_channel_id)
            val channelTypeName = intent?.getStringExtra(SERVICE_NOTIFICATION_CHANNEL_TYPE_NAME)
                ?: getString(R.string.tes_device_service_default_channel_type_name)
            val smallIcon = intent?.getIntExtra(
                SERVICE_NOTIFICATION_SMALL_ICON, R.drawable.ic_launcher_foreground
            ) ?: R.drawable.ic_launcher_foreground
            startForeground(
                123, NotificationUtils.createNotification(
                    this,
                    NotificationUtils.createNotificationChannel(this,
                        object : INotificationChannelType {
                            override fun getChannelDescription(): String {
                                return channelDescription
                            }

                            override fun getChannelId(): String {
                                return channelId
                            }

                            override fun getChannelImportance(): Int {
                                return NotificationManager.IMPORTANCE_HIGH
                            }

                            override fun getChannelTypeName(): String {
                                return channelTypeName
                            }
                        }),
                    title,
                    content,
                    smallIcon = smallIcon,
                )
            )
        }
        isRunning = true
        return super.onStartCommand(intent, flags, startId)
    }


    private fun initDeviceManager(intent: Intent?) {
        if (tesDeviceManager == null) {
            val phoneToDeviceUUid = intent?.getStringExtra(PHONE_TO_DEVICE_UUID) ?: ""
            val deviceToPhoneUUid = intent?.getStringExtra(DEVICE_TO_PHONE_UUID) ?: ""
            tesDeviceManager =
                TesDeviceManager(this.applicationContext, deviceToPhoneUUid, phoneToDeviceUUid)
            tesDeviceManager?.addConnectListener(connectListener)
            tesDeviceManager?.addDisConnectListener(disconnectListener)
        }
    }

    fun getTesDeviceManager() = tesDeviceManager

    override fun onDestroy() {
        super.onDestroy()
        tesDeviceManager?.removeDisConnectListener(connectListener)
        tesDeviceManager?.removeDisConnectListener(disconnectListener)
        tesDeviceManager = null
        mainHandler.removeCallbacksAndMessages(null)
        isRunning = false
    }
}