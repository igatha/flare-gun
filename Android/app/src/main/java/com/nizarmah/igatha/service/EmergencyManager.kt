package com.nizarmah.igatha.service

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.nizarmah.igatha.Constants

class EmergencyManager(context: Context) {

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

    private var confirmationJob: Job? = null
    private val confirmationGracePeriod = Constants.DISASTER_RESPONSE_GRACE_PERIOD

    fun startSOS() {
        if (isSOSAvailable.value && !isSOSActive.value) {
            sosBeacon.startBroadcasting()
            sirenPlayer.startSiren()
        }
    }

    fun stopSOS() {
        sosBeacon.stopBroadcasting()
        sirenPlayer.stopSiren()
        confirmationJob?.cancel()
        confirmationJob = null
    }

    // Starts a coroutine as a confirmation timer
    fun startConfirmationTimer() {
        confirmationJob?.cancel()
        confirmationJob = scope.launch {
            delay(confirmationGracePeriod.toLong())
            startSOS()
        }
    }

    fun deinit() {
        stopSOS()
        sosBeacon.deinit()
        sirenPlayer.deinit()
        scope.cancel()
    }
}
