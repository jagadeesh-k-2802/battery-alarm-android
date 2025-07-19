package com.jackapps.batteryalarm.presentation.alarm_screen

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.jackapps.batteryalarm.presentation.theme.BatteryAlarmTheme
import com.jackapps.batteryalarm.receivers.DismissBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer
    private var currentVolume by Delegates.notNull<Int>()
    private val viewModel: AlarmViewModel by viewModels()

    /* When charger unplugged this exits the activity */
    private val powerDisconnectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = onDismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = viewModel.preferences.value
        turnScreenOnAndKeyguardOff()
        registerReceiver(powerDisconnectedReceiver, IntentFilter(Intent.ACTION_POWER_DISCONNECTED))

        audioManager = getSystemService(AudioManager::class.java)
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

        var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        uri = uri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )

            setDataSource(baseContext, uri)
            prepare()
        }

        if (preferences.shouldSound) {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            val volume = maxVolume * preferences.volumeLevel / 100
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0)
            mediaPlayer.start()
        }

        vibrator = getSystemService(Vibrator::class.java)

        if (preferences.shouldVibrate) {
            val vibratePattern = longArrayOf(1000, 1000)
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, 0))
        }

        setContent {
            BatteryAlarmTheme {
                AlarmScreen(
                    onDismiss = ::onDismiss,
                    viewModel = viewModel
                )
            }
        }
    }

    private fun onDismiss() = finishAndRemoveTask()

    private fun turnScreenOnAndKeyguardOff() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        with(getSystemService(KEYGUARD_SERVICE) as KeyguardManager) {
            requestDismissKeyguard(this@AlarmActivity, null)
        }
    }

    override fun onDestroy() {
        sendBroadcast(Intent(applicationContext, DismissBroadcastReceiver::class.java))
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currentVolume, 0)
        vibrator.cancel()
        mediaPlayer.stop()
        mediaPlayer.release()
        unregisterReceiver(powerDisconnectedReceiver)
        super.onDestroy()
    }
}
