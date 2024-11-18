package com.nizarmah.igatha.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nizarmah.igatha.Constants

class DisasterDetectionService : Service() {
    private lateinit var disasterDetector: DisasterDetector

    override fun onCreate() {
        super.onCreate()

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Initialize the DisasterDetector here
        disasterDetector = DisasterDetector(
            context = this,
            accelerationThreshold = Constants.SENSOR_ACCELERATION_THRESHOLD,
            rotationThreshold = Constants.SENSOR_ROTATION_THRESHOLD,
            pressureThreshold = Constants.SENSOR_PRESSURE_THRESHOLD,
            eventTimeWindow = Constants.DISASTER_TEMPORAL_CORRELATION_TIME_WINDOW.toLong()
        )

        disasterDetector.startDetection()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        disasterDetector.stopDetection()
        disasterDetector.deinit()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitoring for disasters")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)

        return notificationBuilder.build()
    }

    private fun createNotificationChannel(): String {
        val channelId = "disaster_detection_channel"
        val channelName = "Disaster Detection Service"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Used for the disaster detection service"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channelId
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
