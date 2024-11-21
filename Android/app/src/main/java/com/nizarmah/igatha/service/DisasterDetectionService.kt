package com.nizarmah.igatha.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.R
import kotlinx.coroutines.*

class DisasterDetectionService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val emergencyManager = EmergencyManager.getInstance(this)

    private var confirmationJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        emergencyManager.startDetector()

        // Start the service in the foreground with a low-priority notification
        val notification = createNotification()
        startForeground(Constants.DISASTER_MONITORING_NOTIFICATION_ID, notification)

        // Observe disaster detection events
        scope.launch {
            DisasterEventBus.disasterDetectedFlow.collect {
                onDisasterDetected()
            }
        }
    }

    private fun onDisasterDetected() {
        // Show notification to the user
        showDisasterDetectedNotification()

        // Start confirmation timer
        startConfirmationTimer()
    }

    private fun showDisasterDetectedNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = createHighPriorityNotificationChannel()

        // Intent for "I'm Okay" action
        val imOkayIntent = Intent(this, DisasterDetectionService::class.java).apply {
            action = Constants.ACTION_IGNORE_ALERT
        }
        val imOkayPendingIntent = PendingIntent.getService(
            this, 0, imOkayIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for "Need Help" action
        val needHelpIntent = Intent(this, DisasterDetectionService::class.java).apply {
            action = Constants.ACTION_START_SOS
        }
        val needHelpPendingIntent = PendingIntent.getService(
            this, 1, needHelpIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_alerting)
            .setContentTitle("Are you okay?")
            .setContentText("Detected a possible disaster.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_im_okay, "I'm Okay", imOkayPendingIntent)
            .addAction(R.drawable.ic_need_help, "Need Help", needHelpPendingIntent)

        // Show the notification
        notificationManager.notify(Constants.DISASTER_RESPONSE_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun startConfirmationTimer() {
        confirmationJob?.cancel()
        confirmationJob = scope.launch {
            delay((Constants.DISASTER_RESPONSE_GRACE_PERIOD * 1000).toLong())
            startSOSService()
        }
    }

    private fun cancelConfirmationTimer() {
        confirmationJob?.cancel()
        confirmationJob = null
    }

    private fun startSOSService() {
        val sosIntent = Intent(this, SOSService::class.java)
        startForegroundService(sosIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        cancelConfirmationTimer()
        if (intent?.action != null) {
            // Dismiss the alert notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Constants.DISASTER_RESPONSE_NOTIFICATION_ID)
        }

        when (intent?.action) {
            Constants.ACTION_START_SOS -> {
                // Start SOS immediately
                startSOSService()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        cancelConfirmationTimer()
        emergencyManager.stopDetector()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitoring for disasters")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        return notificationBuilder.build()
    }

    private fun createNotificationChannel(): String {
        val channelId = Constants.DISASTER_MONITORING_NOTIFICATION_KEY
        val channelName = "Disaster Detection Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun createHighPriorityNotificationChannel(): String {
        val channelId = Constants.DISASTER_RESPONSE_NOTIFICATION_KEY
        val channelName = "Emergency Alerts"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }
}
