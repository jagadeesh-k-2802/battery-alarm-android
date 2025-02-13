package com.jackapps.batteryalarm.services

import android.Manifest
import android.app.ActivityManager
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.notifications.NOTIFICATION_SERVICE_ID
import com.jackapps.batteryalarm.notifications.buildServiceNotification
import com.jackapps.batteryalarm.presentation.alarm_screen.AlarmActivity
import com.jackapps.batteryalarm.presentation.util.isAndroid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class BatteryAlarmService : Service() {
    private val job = CoroutineScope(Dispatchers.Default)
    private var isActive = false
    private var batteryThreshold by Delegates.notNull<Int>()

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    companion object {
        const val ACTION_STARTED = "action.service_started"
        const val ACTION_STOPPED = "action.service_stopped"
        const val DELAY = 10 * 1000L

        fun toggleService(context: Context) {
            Intent(context, BatteryAlarmService::class.java).also { intent ->
                if (isServiceRunning(context)) {
                    context.stopService(intent)
                } else {
                    context.startForegroundService(intent)
                }
            }
        }

        @Suppress("DEPRECATION")
        fun isServiceRunning(context: Context): Boolean {
            val activityManager = context.getSystemService(ActivityManager::class.java)

            // This still returns the caller's own services.
            val isServiceRunning =
                activityManager.getRunningServices(Int.MAX_VALUE).find { service ->
                    return BatteryAlarmService::class.java.name.equals(service.service.className)
                }

            return isServiceRunning != null
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job.launch {
            sendBroadcast(Intent(ACTION_STARTED))

            preferencesRepository.preferencesFlow.collect { preferences ->
                println("BatteryAlarmService: preferencesFlow.collect: ${preferences}")
                this@BatteryAlarmService.batteryThreshold = preferences.batteryThreshold
                val notification = buildServiceNotification(applicationContext, batteryThreshold)

                if (!this@BatteryAlarmService.isActive) {
                    startForeground(NOTIFICATION_SERVICE_ID, notification)
                    this@BatteryAlarmService.isActive = true
                }

                postNotification(notification)
                monitorBatteryStatus()
            }
        }

        return START_NOT_STICKY
    }

    private fun postNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (isAndroid(Build.VERSION_CODES.TIRAMISU)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(NOTIFICATION_SERVICE_ID, notification)
            }
        } else {
            notificationManager.notify(NOTIFICATION_SERVICE_ID, notification)
        }
    }

    private fun monitorBatteryStatus() {
        job.launch {
            while (isActive) {
                val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intent ->
                    applicationContext.registerReceiver(null, intent)
                }

                val canAlert = batteryStatus?.let { intent ->
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                    val percent = level * 100 / scale

                    val isCharging = plugged == BatteryManager.BATTERY_PLUGGED_AC
                            || plugged == BatteryManager.BATTERY_PLUGGED_USB
                            || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS

                    percent >= batteryThreshold && isCharging
                }

                canAlert?.let {
                    if (canAlert) {
                        Intent(applicationContext, AlarmActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(this)
                            stopSelf()
                        }
                    }
                }

                delay(DELAY)
            }
        }
    }

    override fun onDestroy() {
        isActive = false
        job.cancel()
        sendBroadcast(Intent(ACTION_STOPPED))
        super.onDestroy()
    }
}
