//
//  Models.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import Foundation
import CoreBluetooth

struct Device {
    let peripheral: CBPeripheral
    
    let id: String
    
    init(peripheral: CBPeripheral) {
        self.peripheral = peripheral
        self.id = peripheral.identifier.uuidString
    }
}
