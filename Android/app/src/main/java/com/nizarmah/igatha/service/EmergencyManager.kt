package com.nizarmah.igatha.service

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import com.nizarmah.igatha.Constants
import kotlinx.coroutines.cancel

class EmergencyManager private constructor(context: Context) {
    private val appContext = context.applicationContext

    companion object {
        @Volatile
        private var instance: EmergencyManager? = null

        fun getInstance(context: Context): EmergencyManager {
            return instance ?: synchronized(this) {
                instance ?: EmergencyManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Components initialization
    private val sosBeacon = SOSBeacon(appContext)
    private val sirenPlayer = SirenPlayer(appContext)
    private val disasterDetector = DisasterDetector(
        context = appContext,
        accelerationThreshold = Constants.SENSOR_ACCELERATION_THRESHOLD,
        rotationThreshold = Constants.SENSOR_ROTATION_THRESHOLD,
        pressureThreshold = Constants.SENSOR_PRESSURE_THRESHOLD,
        eventTimeWindow = Constants.DISASTER_TEMPORAL_CORRELATION_TIME_WINDOW.toLong()
    )

    // State management
    val isSOSAvailable: StateFlow<Boolean> = combine(
        sosBeacon.isAvailable,
        sirenPlayer.isAvailable
    ) { beaconAvailable, sirenAvailable ->
        beaconAvailable && sirenAvailable
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val isSOSActive: StateFlow<Boolean> = combine(
        sosBeacon.isActive,
        sirenPlayer.isActive
    ) { beaconActive, sirenActive ->
        beaconActive || sirenActive
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val isDetectorAvailable: StateFlow<Boolean> = combine(
        disasterDetector.isAvailable,
        isSOSAvailable
    ) { detectorAvailable, sosAvailable ->
        detectorAvailable && sosAvailable
    }.stateIn(scope, SharingStarted.Eagerly, false)
    val isDetectorActive: StateFlow<Boolean> = disasterDetector.isActive

    // Core functionality
    fun startSOS() {
        if (!isSOSAvailable.value || isSOSActive.value) return

        sosBeacon.startBroadcasting()
        sirenPlayer.startSiren()
    }

    fun stopSOS() {
        sosBeacon.stopBroadcasting()
        sirenPlayer.stopSiren()
    }

    fun startDetector() {
        if (!isDetectorAvailable.value || isDetectorActive.value) return

        disasterDetector.startDetection()
    }

    fun stopDetector() {
        disasterDetector.stopDetection()
    }

    fun deinit() {
        stopSOS()
        stopDetector()
        sirenPlayer.deinit()
        sosBeacon.deinit()
        disasterDetector.deinit()
        scope.cancel()
    }
}
