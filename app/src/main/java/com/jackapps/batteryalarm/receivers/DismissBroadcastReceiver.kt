package com.jackapps.batteryalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.jackapps.batteryalarm.services.BatteryAlarmService

/*
 * Receiver fired when dismiss action is clicked on notification
 */
class DismissBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.stopService(Intent(context, BatteryAlarmService::class.java))
    }
}
