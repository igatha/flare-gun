package com.nizarmah.igatha

import android.app.Application
import android.content.SharedPreferences
import com.nizarmah.igatha.util.PermissionsManager
import com.nizarmah.igatha.util.SettingsManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import com.nizarmah.igatha.service.FeedbackWorker
import androidx.core.content.edit

class IgathaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        SettingsManager.init(this)
        PermissionsManager.init(this)

        var sharedPrefs = getSharedPreferences(
            Constants.SHARED_PREFERENCES_KEY,
            MODE_PRIVATE
        )

        var workManager = WorkManager.getInstance(this)

        scheduleFeedbackNotification(
            PermissionsManager.notificationsPermitted.value,
            sharedPrefs,
            workManager
        )
    }

    // scheduleFeedbackNotification is used to schedule the feedback notification.
    private fun scheduleFeedbackNotification(
        notificationsPermitted: Boolean,
        sharedPrefs: SharedPreferences,
        workManager: WorkManager
    ) {
        if (
            // Check if notifications are not permitted
            !notificationsPermitted ||
            // Check if feedback notification was already scheduled
            sharedPrefs.contains(Constants.Notifications.Feedback.TIMESTAMP_KEY)
        ) {
            return
        }

        // Schedule feedback notification once using WorkManager
        val feedbackWorkRequest = OneTimeWorkRequestBuilder<FeedbackWorker>()
            .setInitialDelay(
                Constants.Notifications.Feedback.TRIGGER_DELAY,
                TimeUnit.SECONDS
            )
            .build()

        // Enqueue the work request
        workManager
            .enqueueUniqueWork(
                Constants.Notifications.Feedback.ID,
                ExistingWorkPolicy.KEEP,
                feedbackWorkRequest
            )

        // Mark feedback notification as scheduled
        sharedPrefs.edit {
            putLong(
                Constants.Notifications.Feedback.TIMESTAMP_KEY,
                System.currentTimeMillis()
            )
        }
    }
}
