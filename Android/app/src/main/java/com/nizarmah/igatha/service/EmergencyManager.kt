package com.nizarmah.igatha.service

import android.content.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.nizarmah.igatha.Constants

class EmergencyManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val sosBeacon = SOSBeacon(context)
    private val sirenPlayer = SirenPlayer(context)

    // Combine the availability of SOSBeacon and SirenPlayer
    val isSOSAvailable: StateFlow<Boolean> = combine(
        sosBeacon.isAvailable,
        sirenPlayer.isAvailable
    ) { beaconAvailable, sirenAvailable ->
        beaconAvailable && sirenAvailable
    }.stateIn(scope, SharingStarted.Eagerly, false)

    // Combine the active state of SOSBeacon and SirenPlayer
    val isSOSActive: StateFlow<Boolean> = combine(
        sosBeacon.isActive,
        sirenPlayer.isActive
    ) { beaconActive, sirenActive ->
        beaconActive || sirenActive
    }.stateIn(scope, SharingStarted.Eagerly, false)

    // For disaster detection service active state
    private val _isDetectorActive = MutableStateFlow(false)
    val isDetectorActive: StateFlow<Boolean> = _isDetectorActive.asStateFlow()

    // SharedFlow to emit disaster detection events
    private val _disasterDetected = MutableSharedFlow<Unit>(replay = 0)
    val disasterDetected: SharedFlow<Unit> = _disasterDetected.asSharedFlow()

    private var confirmationJob: Job? = null
    private val confirmationGracePeriod = Constants.DISASTER_RESPONSE_GRACE_PERIOD

    init {
        // Observe disaster detection events from DisasterEventBus
        scope.launch {
            DisasterEventBus.disasterDetectedFlow.collect {
                handleDisasterDetected()
            }
        }
    }

    fun startDetector() {
        if (_isDetectorActive.value) return

        val serviceIntent = Intent(context, DisasterDetectionService::class.java)
        context.startForegroundService(serviceIntent)

        _isDetectorActive.value = true
    }

    fun stopDetector() {
        val serviceIntent = Intent(context, DisasterDetectionService::class.java)
        context.stopService(serviceIntent)

        _isDetectorActive.value = false
    }

    fun startSOS() {
        if (!isSOSAvailable.value || isSOSActive.value) return

        sosBeacon.startBroadcasting()
        sirenPlayer.startSiren()
    }

    fun stopSOS() {
        sosBeacon.stopBroadcasting()
        sirenPlayer.stopSiren()

        confirmationJob?.cancel()
        confirmationJob = null
    }

    private fun handleDisasterDetected() {
        scope.launch {
            _disasterDetected.emit(Unit)
        }

        startConfirmationTimer()

        // FIXME: Show notification in the background
    }

    private fun startConfirmationTimer() {
        confirmationJob?.cancel()
        confirmationJob = scope.launch {
            delay(confirmationGracePeriod.toLong())
            startSOS()
        }
    }

    fun deinit() {
        stopSOS()
        stopDetector()
        sosBeacon.deinit()
        sirenPlayer.deinit()
        scope.cancel()
    }
}
