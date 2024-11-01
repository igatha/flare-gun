package com.nizarmah.igatha.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.sqrt

class GyroscopeSensor(
    context: Context,
    override val threshold: Double,
    private val updateInterval: Int // in microseconds
) : com.nizarmah.igatha.sensor.Sensor<Sensor>, SensorEventListener {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override val sensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    override val sensorType: SensorType = SensorType.GYROSCOPE

    override val isAvailable: Boolean
        get() = sensor != null

    private val _events = MutableSharedFlow<SensorCapturedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: SharedFlow<SensorCapturedEvent> = _events.asSharedFlow()

    override fun startUpdates() {
        if (!isAvailable) {
            Log.w(TAG, "Gyroscope sensor not available")
            return
        }

        sensor?.let { gyroscope ->
            sensorManager.registerListener(
                this,
                gyroscope,
                updateInterval
            )
            Log.d(TAG, "GyroscopeSensor: started updates")
        }
    }

    override fun stopUpdates() {
        sensorManager.unregisterListener(this)
        Log.d(TAG, "GyroscopeSensor: stopped updates")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_GYROSCOPE) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val totalRotationRate = sqrt(
            x * x + y * y + z * z
        )

        if (totalRotationRate > threshold) {
            val sensorEvent = SensorCapturedEvent(
                sensorType = sensorType,
                eventTime = event.timestamp
            )
            _events.tryEmit(sensorEvent)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this implementation
    }

    companion object {
        private const val TAG = "GyroscopeSensor"
    }
}
