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
            print("SOSDiscoverer: poweredOn")
        case .poweredOff, .resetting, .unknown:
            // TODO: Improve handling cases
            stopBroadcasting()
            print("SOSDiscoverer: poweredOff | resetting | unknown")
        case .unauthorized:
            // TODO: Improve handling case
            stopBroadcasting()
            print("SOSDiscoverer: unauthorized")
        case .unsupported:
            // TODO: Handle case
            print("SOSDiscoverer: unsupported")
        @unknown default:
            print("SOSDiscoverer: unknown")
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
        
        print("SOSBeacon: broadcasting")
    }
    
    // stop broadcasting as a peripheral
    public func stopBroadcasting() {
        peripheralManager.stopAdvertising()
        peripheralManager.removeAllServices()
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
