package com.nizarmah.igatha.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SOSState {
    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive

    fun setAvailable(available: Boolean) {
        _isAvailable.value = available
    }

    fun setActive(active: Boolean) {
        _isActive.value = active
    }
}
