//
//  Constants.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import CoreBluetooth

struct Constants {
    static let DispatchQueueLabel: String = "me.nizarmah.Igatha"
    
    static let LocationDiscoveryServiceID: CBUUID = CBUUID(
        string: "928278d6-e3d5-42f5-95f8-a6d4fa0a43aa"
    )
    
    static let LatitudeCharacteristicID: CBUUID = CBUUID(
        string: "5e187cdc-0017-48d1-92a5-ad934e7293be"
    )
    static let LongitudeCharacteristicID: CBUUID = CBUUID(
        string: "5e187cdc-0017-48d1-92a5-ad934e7293bf"
    )
}
