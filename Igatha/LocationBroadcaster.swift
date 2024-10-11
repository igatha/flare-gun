//
//  HelpBroadcaster.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 11/10/2024.
//

import Foundation
import CoreBluetooth

class LocationBroadcaster: NSObject, ObservableObject {
    private var locationManager: LocationManager!
    private var peripheralManager: CBPeripheralManager!
    
    @Published public private(set) var broadcastEnabled = false
    
    init(
        with locationManager: LocationManager
    ) {
        super.init()
        
        self.locationManager = locationManager
        self.peripheralManager = CBPeripheralManager(delegate: self, queue: nil)
    }
}

extension LocationBroadcaster: CBPeripheralManagerDelegate {
    // called when the peripheral state changes
    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        switch peripheral.state {
        case .poweredOn:
            // TODO: Improve handling case
            broadcastEnabled = true
            print("LocationBroadcaster: poweredOn")
        case .poweredOff, .resetting, .unknown:
            // TODO: Improve handling cases
            stopBroadcasting()
            print("LocationBroadcaster: poweredOff | resetting | unknown")
        case .unauthorized:
            // TODO: Improve handling case
            stopBroadcasting()
            print("LocationBroadcaster: unauthorized")
        case .unsupported:
            // TODO: Handle case
            print("LocationBroadcaster: unsupported")
        @unknown default:
            print("LocationBroadcaster: unknown")
        }
    }
    
    // start broadcasting (or re-broadcasting) as a peipheral
    public func startBroadcasting() {
        // stop broadcasting if we're already are
        if peripheralManager.isAdvertising {
            stopBroadcasting()
        }
        
        // ensure we're ready to broadcast
        guard
            peripheralManager.state == .poweredOn,
            locationManager.locationEnabled
        else { return }
        
        // setup location discovery service
        let locationDiscoveryService = setupLocationDiscoveryService()
        peripheralManager.add(locationDiscoveryService)
        
        // start broadcasting
        peripheralManager.startAdvertising(
            [
                CBAdvertisementDataServiceUUIDsKey: [
                    locationDiscoveryService.uuid
                ]
            ]
        )
        
        print("LocationBroadcaster: broadcasting")
    }
    
    // stop broadcasting as a peripheral
    public func stopBroadcasting() {
        peripheralManager.stopAdvertising()
        peripheralManager.removeAllServices()
    }
    
    // creates location discovery service with characteristics
    private func setupLocationDiscoveryService() -> CBMutableService {
        let service = CBMutableService(
            type: Constants.LocationDiscoveryServiceID,
            primary: true
        )
        
        service.characteristics = [
            setupLatitudeCharacteristic(),
            setupLongitudeCharacteristic()
        ]
        
        return service
    }
    
    // creates read-only latitude characteristic
    private func setupLatitudeCharacteristic() -> CBMutableCharacteristic {
        var latitude = locationManager.latitude
        let data = withUnsafeBytes(of: &latitude) { Data($0) }
        
        return CBMutableCharacteristic(
            type: Constants.LatitudeCharacteristicID,
            properties: [.read],
            value: data,
            permissions: .readable
        )
    }
    
    // creates read-only longitude characteristic
    private func setupLongitudeCharacteristic() -> CBMutableCharacteristic {
        var longitude = locationManager.longitude
        let data = withUnsafeBytes(of: &longitude) { Data($0) }
        
        return CBMutableCharacteristic(
            type: Constants.LongitudeCharacteristicID,
            properties: [.read],
            value: data,
            permissions: .readable
        )
    }
}
