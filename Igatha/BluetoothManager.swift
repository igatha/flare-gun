//
//  BluetoothManager.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import Foundation
import CoreBluetooth
import CoreLocation

class BluetoothManager: NSObject {
    // list of devices that have been discovered
    private(set) public var devices = [Device]()
    // closure that is called whenever the list of devices is updated
    public var devicesListUpdatedHandler: (() -> Void)?
    
    private var centralManager: CBCentralManager!
    
    private let queue = DispatchQueue(
        label: Constants.DispatchQueueLabel,
        qos: .background,
        attributes: .concurrent,
        autoreleaseFrequency: .workItem,
        target: nil
    )
    
    private var characteristics = [
        Constants.LatitudeCharacteristicID,
        Constants.LongitudeCharacteristicID
    ]
    
    override init() {
        super.init()
        
        self.centralManager = CBCentralManager(delegate: self, queue: nil)
    }
}

extension BluetoothManager: CBCentralManagerDelegate {
    // called when the central state changes
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        guard central.state == .poweredOn else { return }
        
        // start scanning for peripherals
        centralManager.scanForPeripherals(
            withServices: [
                Constants.LocationDiscoveryServiceID
            ],
            options: [
                CBCentralManagerScanOptionAllowDuplicatesKey: false
            ]
        )
    }
    
    // called when a peripheral is detected
    func centralManager(
        _ central: CBCentralManager,
        didDiscover peripheral: CBPeripheral,
        advertisementData: [String: Any],
        rssi RSSI: NSNumber
    ) {
        // initialize the device
        let device = Device(peripheral: peripheral)
        
        // add or update this object to the visible list
        DispatchQueue.main.async { [weak self] in
            self?.updateDeviceList(with: device)
        }
        
        centralManager.connect(peripheral)
    }
    
    // called upon connecting to a peripheral
    func centralManager(
        _ central: CBCentralManager,
        didConnect peripheral: CBPeripheral
    ) {
        peripheral.delegate = self
        peripheral.discoverServices([
            Constants.LocationDiscoveryServiceID
        ])
    }
    
    // if a new device is discovered by the central manager, update the visible list
    fileprivate func updateDeviceList(with device: Device) {
        print("Discovered device \(device.id).")
        
        // if a device already exists in the list, replace it with this new device
        if let index = devices.firstIndex(where: { $0.id == device.id }) {
            guard devices[index].id != device.id else { return }
            devices.remove(at: index)
            devices.insert(device, at: index)
            devicesListUpdatedHandler?()
            return
        }
        
        // if this item didn't exist in the list, append it to the end
        devices.append(device)
        devicesListUpdatedHandler?()
    }
}

extension BluetoothManager: CBPeripheralDelegate {
    func peripheral(
        _ peripheral: CBPeripheral,
        didDiscoverServices error: Error?
    ) {
        if let error = error {
            print("Unable to discover services: \(error.localizedDescription)")
            cleanup(peripheral: peripheral)
            return
        }
        
        peripheral.services?.forEach { service in
            guard service.uuid == Constants.LocationDiscoveryServiceID else { return }
            
            peripheral.discoverCharacteristics(
                characteristics,
                for: service
            )
        }
    }
    
    func peripheral(
        _ peripheral: CBPeripheral,
        didDiscoverCharacteristicsFor service: CBService,
        error: Error?
    ) {
        if let error = error {
            print("Unable to discover characteristics: \(error.localizedDescription)")
            cleanup(peripheral: peripheral)
            return
        }
        
        service.characteristics?.forEach { characteristic in
            guard characteristics.contains(characteristic.uuid) else { return }
            
            peripheral.readValue(for: characteristic)
        }
    }
    
    func peripheral(
        _ peripheral: CBPeripheral,
        didUpdateValueFor characteristic: CBCharacteristic,
        error: Error?
    ) {
        if let error = error {
            print("Unable to read characteristic: \(error.localizedDescription)")
            cleanup(peripheral: peripheral)
            return
        }
        
        guard characteristics.contains(characteristic.uuid) else { return }
        
        guard let data = characteristic.value else { return }
        
        let value = data.withUnsafeBytes { $0.load(as: Double.self) }
        
        print("Read value: \(value)")
    }
    
    private func cleanup(peripheral: CBPeripheral) {
        centralManager.cancelPeripheralConnection(peripheral)
    }
}
