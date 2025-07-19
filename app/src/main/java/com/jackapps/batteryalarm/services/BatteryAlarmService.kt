package com.jackapps.batteryalarm.services

import android.Manifest
import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jackapps.batteryalarm.R
import com.jackapps.batteryalarm.domain.PreferencesRepository
import com.jackapps.batteryalarm.notifications.CHANNEL_SERVICE_ID
import com.jackapps.batteryalarm.notifications.NOTIFICATION_SERVICE_ID
import com.jackapps.batteryalarm.notifications.buildServiceNotification
import com.jackapps.batteryalarm.presentation.alarm_screen.AlarmActivity
import com.jackapps.batteryalarm.presentation.util.isAndroid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class BatteryAlarmService : Service() {

    private val job = CoroutineScope(Dispatchers.Default)
    private var isActive = false
    private var batteryThreshold by Delegates.notNull<Int>()
    private var isFromBoot = false

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    companion object {
        const val TAG = "BatteryAlarmService"
        const val ACTION_STARTED = "action.service_started"
        const val ACTION_STOPPED = "action.service_stopped"
        const val IS_FROM_BOOT = "is_from_boot"
        const val DELAY = 10 * 1000L
        private const val ALARM_NOTIFICATION_ID = 1001

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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        job.launch {
            isFromBoot = intent.getBooleanExtra(IS_FROM_BOOT, false)
            if (isFromBoot) {
                if (!preferencesRepository.preferencesFlow.first().startAtBoot) {
                    return@launch
                }
            }

            sendBroadcast(Intent(ACTION_STARTED))

            preferencesRepository.preferencesFlow.collect { preferences ->
                Log.d(TAG, "preferencesFlow.collect: $preferences")
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
            if (checkNotificationPermission()) {
                notificationManager.notify(NOTIFICATION_SERVICE_ID, notification)
            }
        } else {
            notificationManager.notify(NOTIFICATION_SERVICE_ID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
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
                        handleBatteryAlert()
                    }
                }

                delay(DELAY)
            }
        }
    }

    private fun handleBatteryAlert() {
        val intent = Intent(applicationContext, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // For Android 14+ and boot case, only show notification
        // https://developer.android.com/guide/components/activities/background-starts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && isFromBoot) {
            showBatteryThresholdReachedNotification(createPendingIntent(intent))
        } else {
            // For other cases, try to start activity directly
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ActivityOptions.makeBasic().apply {
                        pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                        applicationContext.startActivity(intent, toBundle())
                    }
                } else {
                    startActivity(intent)
                }
            } catch (_: Exception) {
                // Fallback to notification if activity start fails
                showBatteryThresholdReachedNotification(createPendingIntent(intent))
            }
        }

        stopSelf()
    }

    private fun createPendingIntent(intent: Intent): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val activityOptions = ActivityOptions.makeBasic().apply {
                pendingIntentCreatorBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
            }
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                activityOptions.toBundle()
            )
        } else {
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun showBatteryThresholdReachedNotification(pendingIntent: PendingIntent) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_SERVICE_ID)
            .setContentTitle(getString(R.string.notification_battery_alert_title))
            .setContentText(getString(R.string.notification_battery_alert_text))
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntent, true)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (isAndroid(Build.VERSION_CODES.TIRAMISU)) {
            if (checkNotificationPermission()) {
                notificationManager.notify(ALARM_NOTIFICATION_ID, notification)
            }
        } else {
            notificationManager.notify(ALARM_NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        isActive = false
        job.cancel()
        sendBroadcast(Intent(ACTION_STOPPED))
        super.onDestroy()
    }
}
