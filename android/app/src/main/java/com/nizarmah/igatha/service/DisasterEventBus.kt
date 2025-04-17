package com.nizarmah.igatha.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object DisasterEventBus {
    private val _disasterDetectedFlow = MutableSharedFlow<Unit>(replay = 0)
    val disasterDetectedFlow: SharedFlow<Unit> = _disasterDetectedFlow

    suspend fun emitDisasterDetected() {
        _disasterDetectedFlow.emit(Unit)
    }
}
