package com.nizarmah.igatha.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.R

class FeedbackWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val channel = createNotificationChannel()

        val notificationManager =
            applicationContext.getSystemService(NotificationManager::class.java)

        notificationManager?.createNotificationChannel(channel)

        val notification = sendFeedbackNotification()

        // Display the notification
        NotificationManagerCompat.from(applicationContext)
            .notify(Constants.FEEDBACK_NOTIFICATION_ID, notification)

        return Result.success()
    }

    // createNotificationChannel creates or updates the notification channel for feedback
    private fun createNotificationChannel(): NotificationChannel {
        val channelId = Constants.FEEDBACK_NOTIFICATION_KEY
        val channelName = "Feedback Requests"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        return channel
    }

    // sendFeedbackNotification builds and displays the feedback notification
    private fun sendFeedbackNotification(): Notification {
        val channelId = Constants.FEEDBACK_NOTIFICATION_KEY

        // Build deep link intent using the feedback notification link constant
        val contentUri = Uri.Builder()
            .scheme(Constants.DeepLink.SCHEME)
            .authority(Constants.Notifications.Feedback.LINK.VALUE)
            .build()

        // Build the pending intent
        val intent = Intent(Intent.ACTION_VIEW, contentUri)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Tell us why you use Igatha")
            .setContentText("Help us improve it for you and others")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        return notification
    }
}