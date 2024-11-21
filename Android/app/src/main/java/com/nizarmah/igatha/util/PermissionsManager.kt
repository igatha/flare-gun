package com.nizarmah.igatha.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

object PermissionsManager {
    private val _sosPermitted = MutableStateFlow(false)
    val sosPermitted: StateFlow<Boolean> = _sosPermitted.asStateFlow()

    private val _disasterDetectionPermitted = MutableStateFlow(false)
    val disasterDetectionPermitted: StateFlow<Boolean> = _disasterDetectionPermitted.asStateFlow()

    private val _proximityScanPermitted = MutableStateFlow(false)
    val proximityScanPermitted: StateFlow<Boolean> = _proximityScanPermitted.asStateFlow()

    @OptIn(DelicateCoroutinesApi::class)
    val permissionsGranted: StateFlow<Boolean> = combine(
        sosPermitted,
        disasterDetectionPermitted,
        proximityScanPermitted
    ) { sos, disaster, proximity ->
        sos && disaster && proximity
    }.stateIn(GlobalScope, SharingStarted.Eagerly, false)

    // Initialize by checking current permissions
    fun init(context: Context) {
        refreshPermissions(context)
    }

    // Function to refresh the current permission state
    fun refreshPermissions(context: Context) {
        _sosPermitted.value = hasPermissions(context, PermissionsHelper.getSOSPermissions())
        _proximityScanPermitted.value = hasPermissions(context, PermissionsHelper.getProximityScanPermissions())
        _disasterDetectionPermitted.value = hasPermissions(context, PermissionsHelper.getDisasterDetectionPermissions())
    }

    // Helper function to check permissions
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}
