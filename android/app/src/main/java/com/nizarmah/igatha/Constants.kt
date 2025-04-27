package com.nizarmah.igatha

import android.os.ParcelUuid

object Constants {
    val SOS_BEACON_SERVICE_UUID: ParcelUuid = ParcelUuid.fromString("00001802-0000-1000-8000-00805F9B34FB") // 1802

    // update interval for sensor readings in microseconds
    const val SENSOR_UPDATE_INTERVAL: Int = 100_000
    // threshold for sudden changes in linear acceleration
    // 3.0 g ~= dropping your phone on a hard surface
    const val SENSOR_ACCELERATION_THRESHOLD: Double = 3.0
    // threshold for sudden changes in rotation
    // 6.0 r/s ~= almost a full rotation in 1 second
    const val SENSOR_ROTATION_THRESHOLD: Double = 6.0
    // threshold for sudden changes in atmospheric pressure
    // 0.1 kPa ~= altitude change of approx. 8 to 12 meters
    const val SENSOR_PRESSURE_THRESHOLD: Double = 0.1

    // key for shared preferences collection
    const val SHARED_PREFERENCES_KEY: String = "com.nizarmah.igatha.preferences"
    // key for disaster detector enabled setting in preferences
    const val DISASTER_DETECTION_ENABLED_KEY: String = "disasterDetectionEnabled"
    // time window for temporally correlating sensor readings
    // if all thresholds exceed in 1.5s then we have an incident
    const val DISASTER_TEMPORAL_CORRELATION_TIME_WINDOW: Double = 1.5
    // grace period (seconds) before an incident response is triggered
    const val DISASTER_RESPONSE_GRACE_PERIOD: Double = 120.0

    // percentage of how much a new value should affect the old value
    const val RSSI_EXPONENTIAL_MOVING_AVERAGE_SMOOTHING_FACTOR: Double = 0.18

    const val DISASTER_MONITORING_NOTIFICATION_ID: Int = 1
    const val DISASTER_MONITORING_NOTIFICATION_KEY: String = "DISASTER_MONITORING"
    const val DISASTER_RESPONSE_NOTIFICATION_ID: Int = 2
    const val DISASTER_RESPONSE_NOTIFICATION_KEY: String = "DISASTER_RESPONSE"
    const val DISTRESS_ACTIVE_NOTIFICATION_ID: Int = 3
    const val DISTRESS_ACTIVE_NOTIFICATION_KEY: String = "DISTRESS_ACTIVE"

    const val FEEDBACK_NOTIFICATION_ID: Int = 4
    const val FEEDBACK_NOTIFICATION_KEY: String = "FEEDBACK_REQUEST"

    const val ACTION_IGNORE_ALERT: String = "com.nizarmah.igatha.actions.IGNORE_ALERT"
    const val ACTION_START_SOS: String = "com.nizarmah.igatha.actions.START_SOS"
    const val ACTION_STOP_SOS: String = "com.nizarmah.igatha.actions.STOP_SOS"

    // Deep links
    object DeepLink {
        const val SCHEME = "igatha"

        object Settings {
            const val VALUE = "settings"
        }
    }

    // Notification constants
    object Notifications {
        object Feedback {
            const val ID = "feedbackRequest"
            val LINK = DeepLink.Settings

            // Delay before notification is shown (3 days in seconds)
            const val TRIGGER_DELAY: Long = 3 * 24 * 60 * 60

            // Key for timestamp of when feedback request was scheduled
            const val TIMESTAMP_KEY = "feedbackScheduledTimestamp"
        }
    }
}
