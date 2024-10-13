//
//  HelpBroadcaster.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 11/10/2024.
//

import Foundation
import CoreBluetooth

class SOSBeacon: NSObject, ObservableObject {
    private var peripheralManager: CBPeripheralManager!
    
    @Published public private(set) var broadcastEnabled = false
    @Published public private(set) var isBroadcasting = false
    
    override init() {
        super.init()
        
        self.peripheralManager = CBPeripheralManager(delegate: self, queue: nil)
    }
}

extension SOSBeacon: CBPeripheralManagerDelegate {
    // called when the peripheral state changes
    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        switch peripheral.state {
        case .poweredOn:
            // TODO: Improve handling case
            DispatchQueue.main.async {
                self.broadcastEnabled = true
            }
            print("SOSBeacon: poweredOn")
        case .poweredOff, .resetting, .unknown:
            // TODO: Improve handling cases
            stopBroadcasting()
            print("SOSBeacon: poweredOff | resetting | unknown")
        case .unauthorized:
            // TODO: Improve handling case
            stopBroadcasting()
            print("SOSBeacon: unauthorized")
        case .unsupported:
            // TODO: Handle case
            print("SOSBeacon: unsupported")
        @unknown default:
            print("SOSBeacon: unknown")
        }
    }
    
    // start broadcasting (or re-broadcasting) as a peipheral
    public func startBroadcasting() {
        // stop broadcasting if we're already are
        if peripheralManager.isAdvertising {
            stopBroadcasting()
        }
        
        // ensure we're ready to broadcast
        guard peripheralManager.state == .poweredOn else { return }
        
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
        
        DispatchQueue.main.async {
            self.isBroadcasting = true
        }
        
        print("SOSBeacon: started broadcast")
    }
    
    // stop broadcasting as a peripheral
    public func stopBroadcasting() {
        peripheralManager.stopAdvertising()
        peripheralManager.removeAllServices()
        
        DispatchQueue.main.async {
            self.isBroadcasting = false
        }
        
        print("SOSBeacon: stopped broadcast")
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
