//
//  BluetoothManager.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import Foundation
import CoreBluetooth

class ProximityScanner: NSObject, ObservableObject {
    // map of devices that have been discovered
    @Published public private(set) var devices: [String: Device] = [:]
    
    private var centralManager: CBCentralManager!
    
    private let queue = DispatchQueue(
        label: Constants.DispatchQueueLabel,
        qos: .background,
        attributes: .concurrent,
        autoreleaseFrequency: .workItem,
        target: nil
    )
    
    override init() {
        super.init()
        
        self.centralManager = CBCentralManager(delegate: self, queue: nil)
    }
}

extension ProximityScanner: CBCentralManagerDelegate {
    // called when the central state changes
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            // TODO: Improve handling case
            print("ProximityScanner: poweredOn")
            startScanning()
        case .poweredOff, .resetting, .unknown:
            // TODO: Improve handling cases
            stopScanning()
            print("ProximityScanner: poweredOff | resetting | unknown")
        case .unauthorized:
            // TODO: Improve handling case
            stopScanning()
            print("ProximityScanner: unauthorized")
        case .unsupported:
            // TODO: Handle case
            print("ProximityScanner: unsupported")
        @unknown default:
            print("ProximityScanner: unknown")
        }
    }
    
    func startScanning() {
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
        
        print("ProximityScanner: started scan")
    }
    
    func stopScanning() {
        centralManager.stopScan()
        
        print("ProximityScanner: stopped scan")
    }
    
    // called when a peripheral is detected
    func centralManager(
        _ central: CBCentralManager,
        didDiscover peripheral: CBPeripheral,
        advertisementData: [String: Any],
        rssi RSSI: NSNumber
    ) {
        // initialize the device
        let device = Device(
            id: peripheral.identifier,
            rssi: RSSI.doubleValue,
            lastSeen: Date()
        )
        
        // add or update the device in the map
        DispatchQueue.main.async {
            self.trackDevice(device)
        }
    }
    
    private func trackDevice(
        _ device: Device
    ) {
        if !self.devices.keys.contains(device.id) {
            self.devices[device.id] = device
            return
        }
        
        self.devices[device.id]?.update(
            rssi: device.rssi
        )
    }
}
