package com.jackapps.batteryalarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jackapps.batteryalarm.services.BatteryAlarmService

class BootReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d(TAG, "Device booted up!")
            context.startService(Intent(context, BatteryAlarmService::class.java).apply {
                putExtra(BatteryAlarmService.IS_FROM_BOOT, true)
            })
        }
    }
}
