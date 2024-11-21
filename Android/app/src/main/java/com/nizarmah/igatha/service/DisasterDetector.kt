package com.nizarmah.igatha.service

import android.content.Context
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.sensor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.nizarmah.igatha.sensor.Sensor as InternalSensor

class DisasterDetector(
    context: Context,
    accelerationThreshold: Double,
    rotationThreshold: Double,
    pressureThreshold: Double,
    private val eventTimeWindow: Long // in milliseconds
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val eventTimes = mutableMapOf<SensorType, Long>()

    private val accelerometerSensor = AcceleratorSensor(
        context,
        threshold = accelerationThreshold,
        updateInterval = Constants.SENSOR_UPDATE_INTERVAL
    )
    private val gyroscopeSensor = GyroscopeSensor(
        context,
        threshold = rotationThreshold,
        updateInterval = Constants.SENSOR_UPDATE_INTERVAL
    )
    private val barometerSensor = BarometerSensor(
        context,
        threshold = pressureThreshold,
        updateInterval = Constants.SENSOR_UPDATE_INTERVAL
    )

    private val _isAvailable = MutableStateFlow(true)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    fun startDetection() {
        scope.launch {
            collectSensorEvents(accelerometerSensor)
        }
        scope.launch {
            collectSensorEvents(gyroscopeSensor)
        }
        scope.launch {
            collectSensorEvents(barometerSensor)
        }

        _isActive.value = true
    }

    fun stopDetection() {
        _isActive.value = false

        accelerometerSensor.stopUpdates()
        gyroscopeSensor.stopUpdates()
        barometerSensor.stopUpdates()
        scope.coroutineContext.cancelChildren()
    }

    private suspend fun collectSensorEvents(sensor: InternalSensor) {
        sensor.startUpdates()
        sensor.events.collect { event ->
            eventTimes[event.sensorType] = System.currentTimeMillis()
            checkForIncident()
        }
    }

    private fun checkForIncident() {
        val currentTime = System.currentTimeMillis()
        if (eventTimes.size < 3) return

        if (eventTimes.values.all { currentTime - it <= eventTimeWindow }) {
            // Disaster detected
            scope.launch {
                DisasterEventBus.emitDisasterDetected()
            }
            eventTimes.clear()
        }
    }

    fun deinit() {
        stopDetection()
        scope.cancel()
    }
}
