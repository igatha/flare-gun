package com.nizarmah.igatha.service

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.nizarmah.igatha.Constants

class SOSBeacon(private val context: Context) {
    // StateFlows to expose state changes
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)

            _isActive.value = true
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)

            _isActive.value = false
        }
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED != intent?.action) return

            updateAvailability()
        }
    }

    init {
        initialize()

        // Register the Bluetooth state receiver
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, filter)
    }

    fun deinit() {
        context.unregisterReceiver(bluetoothStateReceiver)

        stopBroadcasting()
    }

    private fun initialize() {
        val bluetoothAdapter = getBluetoothAdapter()
        if (bluetoothAdapter == null) {
            _isAvailable.value = false
            return
        }

        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser

        updateAvailability()
    }

    fun startBroadcasting() {
        if (!_isAvailable.value || _isActive.value) {
            return
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .setTimeout(0)
            .build()

        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(Constants.SOS_BEACON_SERVICE_UUID)
            .setIncludeDeviceName(false)
            .build()

        try {
            bluetoothLeAdvertiser?.startAdvertising(settings, advertiseData, advertiseCallback)
        } catch (e: SecurityException) {
            _isActive.value = false

            Log.e(TAG, "SecurityException in startBroadcasting: ${e.message}")
        }
    }

    fun stopBroadcasting() {
        try {
            bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException in stopBroadcasting: ${e.message}")
        }

        _isActive.value = false
    }

    private fun updateAvailability() {
        val bluetoothAdapter = getBluetoothAdapter()
        if (bluetoothAdapter == null) {
            _isAvailable.value = false
            return
        }

        val isEnabled: Boolean
        val isSupported: Boolean

        try {
            isEnabled = bluetoothAdapter.isEnabled
            isSupported = bluetoothAdapter.isMultipleAdvertisementSupported
        } catch (e: SecurityException) {
            _isAvailable.value = false

            Log.e(TAG, "SecurityException in updateAvailability: ${e.message}")

            return
        }

        _isAvailable.value = isEnabled && isSupported
        if (!_isAvailable.value && _isActive.value) {
            stopBroadcasting()
        }
    }

    private fun getBluetoothAdapter(): BluetoothAdapter? {
        if (!hasBluetoothPermissions()) {
            Log.e(TAG, "Missing Bluetooth permissions")
            return null
        }

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported on this device")
            return null
        }

        return bluetoothAdapter
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 and above
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            // For below Android 12
            true // Permissions are granted at install time
        }
    }

    companion object {
        private const val TAG = "SOSBeacon"
    }
}
