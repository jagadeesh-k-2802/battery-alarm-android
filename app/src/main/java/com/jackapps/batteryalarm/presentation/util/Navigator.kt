package com.jackapps.batteryalarm.presentation.util

import android.app.Activity
import android.content.Intent
import com.jackapps.batteryalarm.presentation.home_screen.MainActivity
import com.jackapps.batteryalarm.presentation.settings_screen.SettingsActivity

object Navigator {
    fun Activity.navigate(screen: Screens) {
        Intent().apply {
            when (screen) {
                Screens.Home -> setClass(baseContext, MainActivity::class.java)
                Screens.Settings -> setClass(baseContext, SettingsActivity::class.java)
            }

            startActivity(this)
        }
    }

    fun Activity.popBackStack() = finish()
}
