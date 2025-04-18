package com.nizarmah.igatha.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.R
import kotlinx.coroutines.*

class SOSService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private lateinit var emergencyManager: EmergencyManager

    override fun onCreate() {
        super.onCreate()

        // Initialize inside onCreate because context is not available earlier
        emergencyManager = EmergencyManager.getInstance(applicationContext)

        startSOS()
    }

    private fun startSOS() {
        emergencyManager.startSOS()

        // Show notification with "Stop SOS" action
        showSOSNotification()
    }

    private fun stopSOS() {
        emergencyManager.stopSOS()

        // Cancel SOS notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.DISTRESS_ACTIVE_NOTIFICATION_ID)

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
