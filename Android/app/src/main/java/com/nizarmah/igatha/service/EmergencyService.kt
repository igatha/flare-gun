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

class EmergencyService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private lateinit var disasterDetector: DisasterDetector
    private lateinit var sosBeacon: SOSBeacon
    private lateinit var sirenPlayer: SirenPlayer

    private var confirmationJob: Job? = null

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

        // Initialize DisasterDetector with appropriate thresholds and time window
        disasterDetector = DisasterDetector(
            context = this,
            accelerationThreshold = Constants.SENSOR_ACCELERATION_THRESHOLD,
            rotationThreshold = Constants.SENSOR_ROTATION_THRESHOLD,
            pressureThreshold = Constants.SENSOR_PRESSURE_THRESHOLD,
            eventTimeWindow = Constants.DISASTER_TEMPORAL_CORRELATION_TIME_WINDOW.toLong()
        )

        // Start the service in the foreground with a low-priority notification
        val notification = createNotification()
        startForeground(Constants.DISASTER_MONITORING_NOTIFICATION_ID, notification)

        // Start disaster detection
        startDisasterDetection()

        // Observe disaster detection events
        scope.launch {
            DisasterEventBus.disasterDetectedFlow.collect {
                onDisasterDetected()
            }
        }
    }

    private fun startDisasterDetection() {
        disasterDetector.startDetection()
    }

    private fun stopDisasterDetection() {
        disasterDetector.stopDetection()
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
        val imOkayIntent = Intent(this, EmergencyService::class.java).apply {
            action = Constants.ACTION_STOP_SOS
        }
        val imOkayPendingIntent = PendingIntent.getService(
            this, 0, imOkayIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for "Need Help" action
        val needHelpIntent = Intent(this, EmergencyService::class.java).apply {
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
            delay(Constants.DISASTER_RESPONSE_GRACE_PERIOD.toLong())
            startSOS()
        }
    }

    private fun cancelConfirmationTimer() {
        confirmationJob?.cancel()
        confirmationJob = null
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
    }

    private fun showSOSNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Intent to stop SOS
        val stopSOSIntent = Intent(this, EmergencyService::class.java).apply {
            action = Constants.ACTION_STOP_SOS
        }
        val stopSOSPendingIntent = PendingIntent.getService(
            this, 2, stopSOSIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, Constants.DISTRESS_ACTIVE_NOTIFICATION_KEY)
            .setSmallIcon(R.drawable.ic_notification_signaling)
            .setContentTitle("Broadcasting SOS")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop_sos, "Stop", stopSOSPendingIntent)

        // Show the notification
        notificationManager.notify(Constants.DISTRESS_ACTIVE_NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_START_SOS -> {
                // Start SOS procedures
                startSOS()

                // Cancel any confirmation timers
                cancelConfirmationTimer()

                // Dismiss any existing alert notifications
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(Constants.DISASTER_RESPONSE_NOTIFICATION_ID)
            }
            Constants.ACTION_STOP_SOS -> {
                // Stop SOS procedures
                stopSOS()

                // User indicated they are okay
                cancelConfirmationTimer()

                // Dismiss the alert notification
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(Constants.DISASTER_RESPONSE_NOTIFICATION_ID)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        stopSOS()
        stopDisasterDetection()
        disasterDetector.deinit()
        sosBeacon.deinit()
        sirenPlayer.deinit()
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
        val channelId = Constants.DISTRESS_ACTIVE_NOTIFICATION_KEY
        val channelName = "Emergency Service"
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
