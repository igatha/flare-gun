package me.nizarmah.igatha.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.nizarmah.igatha.model.Device

class ContentViewModel : ViewModel() {
    private val _isSOSAvailable = MutableStateFlow(false)
    val isSOSAvailable: StateFlow<Boolean> = _isSOSAvailable.asStateFlow()

    private val _isSOSActive = MutableStateFlow(false)
    val isSOSActive: StateFlow<Boolean> = _isSOSActive.asStateFlow()

    private val _activeAlert = MutableStateFlow<AlertType?>(null)
    val activeAlert: StateFlow<AlertType?> = _activeAlert.asStateFlow()

    private val _devicesMap = MutableStateFlow<Map<String, Device>>(emptyMap())
    val devices: StateFlow<List<Device>> = MutableStateFlow(emptyList())

    init {
        updateSOSAvailability()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun startDetector() {
        // TODO: Add logic
    }

    fun stopDetector() {
        // TODO: Add logic
    }

    fun startSOS() {
        // TODO: Add logic
    }

    fun stopSOS() {
        // TODO: Add logic
    }

    private fun updateSOSAvailability(
        isAvailable: Boolean? = null,
        isActive: Boolean? = null
    ) {
        // TODO: Add logic
    }
}

sealed class AlertType(val id: Int) {
    object SOSConfirmation : AlertType(1)
    object DisasterDetected : AlertType(2)
}
