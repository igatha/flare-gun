package me.nizarmah.igatha.sensor

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

class AcceleratorSensor(
    context: Context,
    override val threshold: Double,
    private val updateInterval: Int // in microseconds
) : me.nizarmah.igatha.sensor.Sensor<Sensor>, SensorEventListener {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override val sensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override val sensorType: SensorType = SensorType.ACCELEROMETER

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
            Log.w(TAG, "Accelerometer sensor not available")
            return
        }

        sensor?.let { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                updateInterval
            )
            Log.d(TAG, "AcceleratorSensor: started updates")
        }
    }

    override fun stopUpdates() {
        sensorManager.unregisterListener(this)
        Log.d(TAG, "AcceleratorSensor: stopped updates")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val totalAcceleration = sqrt(
            x * x + y * y + z * z
        )

        if (totalAcceleration > threshold) {
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
        private const val TAG = "AcceleratorSensor"
    }
}
