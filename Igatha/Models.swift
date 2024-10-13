//
//  Models.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import Foundation
import CoreBluetooth

extension UUID {
    // returns the first 8 chars from a uuid string
    var shortName: String {
        return self.uuidString.prefix(8).lowercased()
    }
}

struct Device: Identifiable {
    let peripheral: CBPeripheral
    
    let rssi: Double
    
    let lastSeen: Date
    
    let id: String
    let shortName: String
    
    init(
        peripheral: CBPeripheral,
        rssi: Double,
        lastSeen: Date = .now
    ) {
        self.peripheral = peripheral
        
        self.rssi = rssi
        
        self.lastSeen = lastSeen
        
        self.id = peripheral.identifier.uuidString
        self.shortName = peripheral.identifier.shortName
    }
    
    func estimateDistance(
        using pathLossExponent: PathLossExponent = .urban
    ) -> Double {
        // approximate RSSI at 1 meter
        let txPower: Double = -59.0

        // path-loss exponent
        let n: Double = pathLossExponent.value
        
        let distance = pow(10.0, (txPower - rssi) / (10.0 * n))
        
        // round to the nearest hundredth for simplicity
        return (distance * 100.0).rounded() / 100.0
    }
}

enum PathLossExponent {
    // path-loss exponent (n)
    
    // free open spaces
    // n = 2.0
    case freeSpace
    
    // indoor environments
    // n = 3.0
    case indoor
    
    // dense urban environments
    // n = 4.0
    case urban

    var value: Double {
        switch self {
        case .freeSpace:
            return 2.0
        case .indoor:
            return 3.0
        case .urban:
            return 4.0
        }
    }
}
