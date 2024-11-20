package com.nizarmah.igatha.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.R
import com.nizarmah.igatha.state.SOSState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine

class SOSService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private lateinit var sosBeacon: SOSBeacon
    private lateinit var sirenPlayer: SirenPlayer

    override fun onCreate() {
        super.onCreate()

        // Initialize SOSBeacon and SirenPlayer
        sosBeacon = SOSBeacon(this)
        sirenPlayer = SirenPlayer(this)

        // Start collecting availability states
        scope.launch {
            combine(
                sosBeacon.isAvailable,
                sirenPlayer.isAvailable
            ) { beaconAvailable, sirenAvailable ->
                beaconAvailable && sirenAvailable
            }.collect { isAvailable ->
                SOSState.setAvailable(isAvailable)
            }
        }

        // Start SOS procedures
        startSOS()
    }

    private fun startSOS() {
        // Start SOS procedures
        sosBeacon.startBroadcasting()
        sirenPlayer.startSiren()

        // Show notification with "Stop SOS" action
        showSOSNotification()

        // Update SOS state
        SOSState.setActive(true)
    }

    private fun stopSOS() {
        // Stop SOS procedures
        sosBeacon.stopBroadcasting()
        sirenPlayer.stopSiren()

        // Cancel SOS notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.DISTRESS_ACTIVE_NOTIFICATION_ID)

        // Update SOS state
        SOSState.setActive(false)

        // Stop the service
        stopSelf()
    }

    private fun showSOSNotification() {
        val channelId = createNotificationChannel()

        // Intent to stop SOS
        val stopSOSIntent = Intent(this, SOSService::class.java).apply {
            action = Constants.ACTION_STOP_SOS
        }
        val stopSOSPendingIntent = PendingIntent.getService(
            this, 0, stopSOSIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_signaling)
            .setContentTitle("Broadcasting SOS")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop_sos, "Stop", stopSOSPendingIntent)

        // Start the service in the foreground
        startForeground(Constants.DISTRESS_ACTIVE_NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_STOP_SOS -> {
                // Stop SOS procedures
                stopSOS()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        stopSOS()
        sosBeacon.deinit()
        sirenPlayer.deinit()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel(): String {
        val channelId = Constants.DISTRESS_ACTIVE_NOTIFICATION_KEY
        val channelName = "SOS Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }
}
