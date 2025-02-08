package com.jackapps.batteryalarm.presentation.util

import android.os.Build

fun isAndroid(version: Int): Boolean {
    return Build.VERSION.SDK_INT >= version
}
