package com.nizarmah.igatha.util

import android.Manifest
import android.os.Build
import kotlin.collections.plus

object PermissionsHelper {
    fun getNotificationsPermissions(): Array<String> {
        var permissions = emptyArray<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        return permissions
    }

    fun getSOSPermissions(): Array<String> {
        var permissions = arrayOf(
            // SOS Beacon
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            // Siren Player
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions += arrayOf(
                // SOS Beacon
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        }

        permissions += getServicePermissions()

        return permissions
    }

    fun getProximityScanPermissions(): Array<String> {
        var permissions = arrayOf(
            // ProximityScanner
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions += arrayOf(
                // ProximityScanner
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            permissions += arrayOf(
                // ProximityScanner
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        return permissions
    }

    fun getDisasterDetectionPermissions(): Array<String> {
        var permissions = emptyArray<String>()

        permissions += getServicePermissions()

        return permissions
    }

    private fun getServicePermissions(): Array<String> {
        var permissions = emptyArray<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions += arrayOf(
                Manifest.permission.FOREGROUND_SERVICE
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions += arrayOf(
                Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions += arrayOf(
                Manifest.permission.FOREGROUND_SERVICE_HEALTH
            )
        }

        // Foreground services need a notification
        // So, check notification permissions as well
        permissions += getNotificationsPermissions()

        return permissions
    }
}
