//
//  SOSBeacon.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 11/10/2024.
//

import Foundation
import CoreBluetooth

class SOSBeacon: NSObject {
    weak var delegate: SOSBeaconDelegate?
    
    private var peripheralManager: CBPeripheralManager!
    
    public var isActive: Bool {
        return peripheralManager.isAdvertising
    }
    public var isAvailable: Bool = false
    
    override init() {
        super.init()
        
        peripheralManager = CBPeripheralManager(
            delegate: self,
            queue: nil,
            options: [
                CBPeripheralManagerOptionRestoreIdentifierKey: Constants.SOSBeaconRestoreID
            ]
        )
    }
    
    deinit {
        stopBroadcasting()
    }
    
    // start broadcasting (or re-broadcasting) as a peipheral
    public func startBroadcasting() {
        guard
            isAvailable
                && !isActive
        else { return }
        
        // start broadcasting
        peripheralManager.startAdvertising(
            [
                CBAdvertisementDataServiceUUIDsKey: [
                    Constants.SOSBeaconServiceID
                ]
            ]
        )
        
        delegate?.beaconStarted()
    }
    
    // stop broadcasting as a peripheral
    public func stopBroadcasting() {
        peripheralManager.stopAdvertising()
        peripheralManager.removeAllServices()
        
        delegate?.beaconStopped()
    }
}

extension SOSBeacon: CBPeripheralManagerDelegate {
    // called when the peripheral state changes
    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        switch peripheral.state {
        case .poweredOn:
            isAvailable = true
            
        case .poweredOff, .resetting, .unauthorized, .unsupported, .unknown:
            isAvailable = false
            
        @unknown default:
            isAvailable = false
        }
        
        delegate?.beaconAvailabilityUpdate(isAvailable)
        
        if !isAvailable {
            stopBroadcasting()
        }
    }
    
    func peripheralManager(
        _ peripheral: CBPeripheralManager,
        willRestoreState dict: [String: Any]
    ) {
        // check if it was advertising
        guard
            let serviceUUIDs = dict[CBAdvertisementDataServiceUUIDsKey] as? [CBUUID],
            serviceUUIDs.contains(Constants.SOSBeaconServiceID)
        else {
            return
        }
        
        startBroadcasting()
    }
}

protocol SOSBeaconDelegate: AnyObject {
    func beaconStarted()
    func beaconStopped()
    
    func beaconAvailabilityUpdate(_ isAvailable: Bool)
}
