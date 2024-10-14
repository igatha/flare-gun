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
        
        self.peripheralManager = CBPeripheralManager(delegate: self, queue: nil)
    }
    
    deinit {
        stopBroadcasting()
    }
}

extension SOSBeacon: CBPeripheralManagerDelegate {
    // called when the peripheral state changes
    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        switch peripheral.state {
        case .poweredOn:
            isAvailable = true
            delegate?.beaconAvailabilityUpdate(true)
            
        case .poweredOff, .resetting, .unauthorized, .unsupported, .unknown:
            isAvailable = false
            delegate?.beaconAvailabilityUpdate(false)
            stopBroadcasting()
            
        @unknown default:
            isAvailable = false
            delegate?.beaconAvailabilityUpdate(false)
            stopBroadcasting()
        }
    }
    
    // start broadcasting (or re-broadcasting) as a peipheral
    public func startBroadcasting() {
        guard
            isAvailable
                && !isActive
        else { return }
        
        // setup sos beacon service
        let sosBeaconService = setupSOSBeaconService()
        peripheralManager.add(sosBeaconService)
        
        // start broadcasting
        peripheralManager.startAdvertising(
            [
                CBAdvertisementDataServiceUUIDsKey: [
                    sosBeaconService.uuid
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
    
    // creates sos beacon service
    private func setupSOSBeaconService() -> CBMutableService {
        let service = CBMutableService(
            type: Constants.SOSBeaconServiceID,
            primary: true
        )
        
        return service
    }
}

protocol SOSBeaconDelegate: AnyObject {
    func beaconStarted()
    func beaconStopped()
    
    func beaconAvailabilityUpdate(_ isAvailable: Bool)
}
