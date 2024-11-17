package com.nizarmah.igatha.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.model.Device
import java.util.Date
import java.util.UUID

class ProximityScanner(private val context: Context) {
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private val _scannedDevices = MutableStateFlow<Device?>(null)
    val scannedDevices: StateFlow<Device?> = _scannedDevices.asStateFlow()

    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val macAddress = result.device.address
            val uuid = UUID.nameUUIDFromBytes(macAddress.toByteArray())

            val device = Device(
                id = uuid,
                rssi = result.rssi.toDouble(),
                lastSeen = Date()
            )

            _scannedDevices.value = device
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scanning failed with error: $errorCode")
            stopScanning()
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

        // Register for Bluetooth state changes
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, filter)
    }

    fun deinit() {
        context.unregisterReceiver(bluetoothStateReceiver)
        stopScanning()
    }

    private fun initialize() {
        val bluetoothAdapter = getBluetoothAdapter()
        if (bluetoothAdapter == null) {
            _isAvailable.value = false
            return
        }

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        updateAvailability()
    }

    fun startScanning() {
        if (!_isAvailable.value || _isActive.value) {
            return
        }

        val scanFilters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(Constants.SOS_BEACON_SERVICE_UUID)
                .build()
        )

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
            _isActive.value = true
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException in startScanning: ${e.message}")
            _isActive.value = false
        }
    }

    fun stopScanning() {
        try {
            bluetoothLeScanner?.stopScan(scanCallback)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException in stopScanning: ${e.message}")
        }

        _isActive.value = false
    }

    private fun updateAvailability() {
        val bluetoothAdapter = getBluetoothAdapter()
        if (bluetoothAdapter == null) {
            _isAvailable.value = false
            return
        }

        try {
            _isAvailable.value = bluetoothAdapter.isEnabled
            if (!_isAvailable.value && _isActive.value) {
                stopScanning()
            }
        } catch (e: SecurityException) {
            _isAvailable.value = false
            Log.e(TAG, "SecurityException in updateAvailability: ${e.message}")
        }
    }

    private fun getBluetoothAdapter(): BluetoothAdapter? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    companion object {
        private const val TAG = "ProximityScanner"
    }
}
