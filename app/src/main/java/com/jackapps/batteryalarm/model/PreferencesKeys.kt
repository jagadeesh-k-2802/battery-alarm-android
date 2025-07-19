package com.jackapps.batteryalarm.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val THEME = stringPreferencesKey("theme")
    val BATTERY_THRESHOLD = intPreferencesKey("battery_threshold")
    val SHOULD_VIBRATE = booleanPreferencesKey("should_vibrate")
    val SHOULD_SOUND = booleanPreferencesKey("should_sound")
    val VOLUME_LEVEL = intPreferencesKey("volume_level")
    val START_AT_BOOT = booleanPreferencesKey("start_at_boot")
}
