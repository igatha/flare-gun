//
//  ProximityScanner.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import Foundation
import CoreBluetooth

class ProximityScanner: NSObject {
    weak var delegate: ProximityScannerDelegate?
    
    private var centralManager: CBCentralManager!
    
    public var isActive: Bool {
        return centralManager.isScanning
    }
    public var isAvailable: Bool = false
    
    override init() {
        super.init()
        
        centralManager = CBCentralManager(delegate: self, queue: nil)
    }
    
    deinit {
        stopScanning()
    }
}

extension ProximityScanner: CBCentralManagerDelegate {
    // called when the central state changes
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            isAvailable = true
            
        case .poweredOff, .resetting, .unauthorized, .unsupported, .unknown:
            isAvailable = false
            
        @unknown default:
            isAvailable = false
        }
        
        delegate?.scannerAvailabilityUpdate(isAvailable)
        
        if !isAvailable {
            stopScanning()
        } else {
            startScanning()
        }
    }
    
    func startScanning() {
        guard
            isAvailable
                && !isActive
        else {
            return
        }
        
        // start scanning for peripherals
        centralManager.scanForPeripherals(
            withServices: [
                Constants.SOSBeaconServiceID
            ],
            options: [
                // allowing duplicates updates the rssi value
                CBCentralManagerScanOptionAllowDuplicatesKey: true
            ]
        )
    }
    
    func stopScanning() {
        centralManager.stopScan()
    }
    
    // called when a peripheral is detected
    func centralManager(
        _ central: CBCentralManager,
        didDiscover peripheral: CBPeripheral,
        advertisementData: [String: Any],
        rssi RSSI: NSNumber
    ) {
        delegate?.scannedDevice(
            Device(
                id: peripheral.identifier,
                rssi: RSSI.doubleValue,
                lastSeen: Date()
            )
        )
    }
}

protocol ProximityScannerDelegate: AnyObject {
    func scannedDevice(_ device: Device)
    
    func scannerAvailabilityUpdate(_ isAvailable: Bool)
}
