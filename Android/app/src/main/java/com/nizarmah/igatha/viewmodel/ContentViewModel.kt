package com.nizarmah.igatha.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.model.Device
import com.nizarmah.igatha.service.DisasterDetectionService
import com.nizarmah.igatha.service.EmergencyManager
import com.nizarmah.igatha.service.ProximityScanner
import com.nizarmah.igatha.service.SOSService
import com.nizarmah.igatha.util.PermissionsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContentViewModel(app: Application) : AndroidViewModel(app) {

    private val emergencyManager = EmergencyManager.getInstance(getApplication())
    private val proximityScanner = ProximityScanner(getApplication())

    // SOS state
    val isSOSAvailable: StateFlow<Boolean> = emergencyManager.isSOSAvailable
    val isSOSActive: StateFlow<Boolean> = emergencyManager.isSOSActive

    // ProximityScanner state
    val isProximityScanAvailable: StateFlow<Boolean> = combine(
        proximityScanner.isAvailable,
        PermissionsManager.proximityScanPermitted
    ) { isAvailable, isPermitted ->
        isAvailable && isPermitted
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Active alert state
    private val _activeAlert = MutableStateFlow<AlertType?>(null)
    val activeAlert: StateFlow<AlertType?> = _activeAlert.asStateFlow()

    private val _devicesMap = MutableStateFlow<Map<String, Device>>(emptyMap())
    val devices: StateFlow<List<Device>> = _devicesMap.asStateFlow()
        .map { devicesMap ->
            devicesMap.values.sortedByDescending { it.rssi }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    init {
        // Observe the disaster detection availability
        viewModelScope.launch {
            emergencyManager.isDetectorAvailable.collect { available ->
                if (available) {
                    startDisasterDetectionService()
                } else {
                    stopDisasterDetectionService()
                }
            }
        }

        // Observe proximity scanner's scanned devices
        viewModelScope.launch {
            proximityScanner.scannedDevices.collect { device ->
                device?.let { updateDevice(it) }
            }
        }

        // Start or stop the scanner based on availability
        viewModelScope.launch {
            isProximityScanAvailable.collect { available ->
                if (available) {
                    proximityScanner.startScanning()
                } else {
                    proximityScanner.stopScanning()
                }
            }
        }
    }

    private fun startDisasterDetectionService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, DisasterDetectionService::class.java)
        context.startForegroundService(serviceIntent)
    }

    private fun stopDisasterDetectionService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, DisasterDetectionService::class.java)
        context.stopService(serviceIntent)
    }

    private fun startSOSService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, SOSService::class.java)
        context.startForegroundService(serviceIntent)
    }

    private fun stopSOSService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, SOSService::class.java).apply {
            action = Constants.ACTION_STOP_SOS
        }
        context.startService(serviceIntent)
    }

    fun startSOS() {
        if (!isSOSAvailable.value || isSOSActive.value) {
            return
        }

        startSOSService()
    }

    fun stopSOS() {
        stopSOSService()
    }

    fun showSOSConfirmation() {
        if (!isSOSAvailable.value || isSOSActive.value) {
            return
        }

        _activeAlert.value = AlertType.SOSConfirmation
    }

    fun dismissAlert() {
        _activeAlert.value = null
    }

    private fun updateDevice(device: Device) {
        _devicesMap.update { currentMap ->
            val updatedMap = currentMap.toMutableMap()
            updatedMap[device.id] = device
            updatedMap
        }
    }

    override fun onCleared() {
        super.onCleared()
        proximityScanner.deinit()
    }
}

sealed class AlertType(id: Int) {
    object SOSConfirmation : AlertType(1)
}
