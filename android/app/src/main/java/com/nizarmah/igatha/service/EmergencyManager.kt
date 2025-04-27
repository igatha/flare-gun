package com.nizarmah.igatha.service

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.util.PermissionsManager
import com.nizarmah.igatha.util.SettingsManager
import kotlinx.coroutines.cancel

class EmergencyManager private constructor(context: Context) {
    private val appContext = context.applicationContext

    companion object {
        @Volatile
        private var instance: EmergencyManager? = null

        fun getInstance(ctx: Context?): EmergencyManager {
            val appCtx = ctx?.applicationContext
                ?: throw IllegalStateException("EmergencyManager requested with null Context")

            return instance ?: synchronized(this) {
                instance ?: EmergencyManager(appCtx).also { instance = it }
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
        sirenPlayer.isAvailable,
        PermissionsManager.sosPermitted
    ) { beacon, siren, permitted ->
        beacon && siren && permitted
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val isSOSActive: StateFlow<Boolean> = combine(
        sosBeacon.isActive,
        sirenPlayer.isActive
    ) { beacon, siren ->
        beacon || siren
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val isDetectorAvailable: StateFlow<Boolean> = combine(
        disasterDetector.isAvailable,
        isSOSAvailable,
        PermissionsManager.disasterDetectionPermitted
    ) { detector, sos, permitted ->
        detector && sos && permitted
    }.stateIn(scope, SharingStarted.Eagerly, false)
    val isDetectorEnabled: StateFlow<Boolean> = combine(
        isDetectorAvailable,
        SettingsManager.disasterDetectionEnabled
    ) { available, enabled ->
        available && enabled
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

    fun startDetector(): Boolean {
        if (!isDetectorEnabled.value) return false

        if (isDetectorActive.value) return true

        disasterDetector.startDetection()
        return true
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
