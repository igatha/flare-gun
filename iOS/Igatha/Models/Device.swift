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
        return self.uuidString.prefix(8).uppercased()
    }
}

class Device: Identifiable, ObservableObject {
    let id: String
    let shortName: String
    
    @Published var rssi: Double
    @Published var lastSeen: Date
    
    init(
        id: UUID,
        rssi: Double,
        lastSeen: Date = Date()
    ) {
        self.id = id.uuidString
        self.shortName = id.shortName
        
        self.rssi = rssi
        self.lastSeen = lastSeen
    }
    
    func update(
        rssi: Double,
        lastSeen: Date = Date()
    ) {
        let oldRSSI = self.rssi
        let newRSSI = rssi
        
        // smoothing factor
        let alpha = Constants.RSSIExponentialMovingAverageSmoothingFactor
        
        // smoothen the RSSI with exponential moving average
        let smoothedRSSI = alpha * newRSSI + (1 - alpha) * oldRSSI
        
        self.rssi = smoothedRSSI
        self.lastSeen = lastSeen
    }
    
    func estimateDistance(
        using pathLossExponent: PathLossExponent = .urban
    ) -> Double {
        // 1 meter ~= -59.0 RSSI
        let txPower = -59.0
        
        // path-loss exponent
        let n: Double = pathLossExponent.value
        
        let distance = pow(10.0, (txPower - rssi) / (10.0 * n))
        
        // round for simplicity simplicity
        return (distance * 1000.0).rounded() / 1000.0
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
