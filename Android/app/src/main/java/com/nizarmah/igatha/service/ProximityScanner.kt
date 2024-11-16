package com.nizarmah.igatha.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.nizarmah.igatha.Constants
import com.nizarmah.igatha.model.Device
import java.util.Date
import java.util.UUID

class ProximityScanner(private val context: Context) {
    interface Delegate {
        fun scannedDevice(device: Device)
        fun scannerAvailabilityUpdate(isAvailable: Boolean)
    }

    var delegate: Delegate? = null

    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private var isScanning = false

    val isActive: Boolean
        get() = isScanning && bluetoothLeScanner != null

    var isAvailable: Boolean = false
        private set

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val device = Device(
                id = UUID.fromString(result.device.address.replace(":", "")),
                rssi = result.rssi.toDouble(),
                lastSeen = Date()
            )

            delegate?.scannedDevice(device)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan failed with error: $errorCode")
            stopScanning()
        }
    }

    init {
        initialize()
    }

    private fun initialize() {
        val bluetoothAdapter = getBluetoothAdapter()
        if (bluetoothAdapter == null) {
            isAvailable = false
            return
        }

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        updateAvailability()
    }

    fun startScanning() {
        if (!isAvailable || isActive) {
            return
        }

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(Constants.SOS_BEACON_SERVICE_UUID)
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            bluetoothLeScanner?.startScan(
                listOf(scanFilter),
                settings,
                scanCallback
            )
            isScanning = true
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException starting scan: ${e.message}")
        }
    }

    fun stopScanning() {
        try {
            bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException stopping scan: ${e.message}")
        }
    }

    private fun updateAvailability() {
        val bluetoothAdapter = getBluetoothAdapter()
        if (bluetoothAdapter == null) {
            isAvailable = false
            return
        }

        try {
            isAvailable = bluetoothAdapter.isEnabled
        } catch (e: SecurityException) {
            isAvailable = false
            Log.e(TAG, "SecurityException in updateAvailability: ${e.message}")
            return
        }

        delegate?.scannerAvailabilityUpdate(isAvailable)

        if (!isAvailable && isActive) {
            stopScanning()
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
