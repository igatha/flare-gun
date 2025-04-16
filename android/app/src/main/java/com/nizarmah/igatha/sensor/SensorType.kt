package com.nizarmah.igatha.sensor

import android.hardware.Sensor
import kotlinx.coroutines.flow.SharedFlow

enum class SensorType {
    ACCELEROMETER,
    GYROSCOPE,
    BAROMETER
}

interface AnySensor {
    val isAvailable: Boolean

    fun startUpdates()
    fun stopUpdates()
}

interface Sensor : AnySensor {
    val sensor: Sensor?
    val threshold: Double
    val sensorType: SensorType

    // A shared flow to emit events when the threshold is exceeded
    val events: SharedFlow<SensorCapturedEvent>
}

data class SensorCapturedEvent(
    val sensorType: SensorType,
    val eventTime: Long
)
