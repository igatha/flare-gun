package com.nizarmah.igatha.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.UserSettings
import com.nizarmah.igatha.model.Device
import com.nizarmah.igatha.service.DisasterDetectionService
import com.nizarmah.igatha.service.ProximityScanner
import com.nizarmah.igatha.service.SOSService
import com.nizarmah.igatha.state.SOSState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContentViewModel(app: Application) : AndroidViewModel(app) {

    // SOS state
    val isSOSAvailable: StateFlow<Boolean> = SOSState.isAvailable
    val isSOSActive: StateFlow<Boolean> = SOSState.isActive

    // Active alert state
    private val _activeAlert = MutableStateFlow<AlertType?>(null)
    val activeAlert: StateFlow<AlertType?> = _activeAlert.asStateFlow()

    // ProximityScanner and devices list
    private val proximityScanner = ProximityScanner(getApplication())

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
        // Observe the disaster detection setting
        viewModelScope.launch {
            UserSettings.disasterDetectionEnabled.collect { enabled ->
                if (enabled) {
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
            proximityScanner.isAvailable.collect { isAvailable ->
                if (isAvailable) {
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
