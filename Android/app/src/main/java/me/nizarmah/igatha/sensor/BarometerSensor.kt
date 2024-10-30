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
import kotlin.math.abs

class BarometerSensor(
    context: Context,
    override val threshold: Double,
    private val updateInterval: Int // in microseconds
) : me.nizarmah.igatha.sensor.Sensor<Sensor>, SensorEventListener {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override val sensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    override val sensorType: SensorType = SensorType.BAROMETER

    override val isAvailable: Boolean
        get() = sensor != null

    private var initialPressure: Float? = null

    private val _events = MutableSharedFlow<SensorCapturedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: SharedFlow<SensorCapturedEvent> = _events.asSharedFlow()

    override fun startUpdates() {
        if (!isAvailable) {
            Log.w(TAG, "Barometer sensor not available")
            return
        }

        initialPressure = null

        sensor?.let { barometer ->
            sensorManager.registerListener(
                this,
                barometer,
                updateInterval
            )
            Log.d(TAG, "BarometerSensor: started updates")
        }
    }

    override fun stopUpdates() {
        sensorManager.unregisterListener(this)
        initialPressure = null
        Log.d(TAG, "BarometerSensor: stopped updates")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_PRESSURE) return

        val pressure = event.values[0] // pressure in hPa (millibar)

        if (initialPressure == null) {
            initialPressure = pressure
            return
        }

        val pressureChange = abs(pressure - (initialPressure ?: return))

        if (pressureChange > threshold) {
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
        private const val TAG = "BarometerSensor"
    }
}