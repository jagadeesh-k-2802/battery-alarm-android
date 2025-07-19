package com.jackapps.batteryalarm.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.jackapps.batteryalarm.domain.PreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/* Service to manage the alarm quick setting tile */
@AndroidEntryPoint
class BatteryAlarmTileService : TileService() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onClick() {
        super.onClick()
        BatteryAlarmService.toggleService(applicationContext)
        updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile
        val isServiceRunning = BatteryAlarmService.isServiceRunning(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runBlocking {
                val preferences = preferencesRepository.preferencesFlow.first()
                tile.subtitle = if (isServiceRunning) "${preferences.batteryThreshold}%" else ""
            }
        }

        tile.state = if (isServiceRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}
