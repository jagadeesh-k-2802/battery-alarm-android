package com.jackapps.batteryalarm.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jackapps.batteryalarm.R
import com.jackapps.batteryalarm.presentation.home_screen.MainActivity
import com.jackapps.batteryalarm.receivers.DismissBroadcastReceiver

const val NOTIFICATION_SERVICE_ID = 1
const val CHANNEL_SERVICE_ID = "foreground_service"
const val CHANNEL_NAME = "Foreground Service"

fun buildServiceNotification(
    context: Context,
    batteryThreshold: Int
): Notification {
    val channel = NotificationChannel(
        CHANNEL_SERVICE_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
    )

    channel.name = context.getString(R.string.notification_alarm_channel_name)
    channel.description = context.getString(R.string.notification_alarm_channel_desc)
    NotificationManagerCompat.from(context).createNotificationChannel(channel)

    val mainActivityIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_SERVICE_ID,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    val stopPendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_SERVICE_ID,
        Intent(context, DismissBroadcastReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    val stopAction = Notification.Action.Builder(
        Icon.createWithResource(context, R.drawable.ic_cancel),
        context.getString(R.string.notification_alarm_action_stop),
        stopPendingIntent
    )

    return Notification.Builder(context, CHANNEL_SERVICE_ID)
        .setContentTitle(context.getString(R.string.notification_alarm_title))
        .setContentText(context.getString(R.string.notification_alarm_text, batteryThreshold))
        .setContentIntent(mainActivityIntent)
        .setCategory(NotificationCompat.CATEGORY_PROGRESS)
        .setShowWhen(true)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.ic_notification)
        .addAction(stopAction.build())
        .setOngoing(true)
        .build()
}
