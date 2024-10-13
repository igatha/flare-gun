//
//  Constants.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 06/10/2024.
//

import CoreBluetooth

struct Constants {
    static let DispatchQueueLabel: String = "me.nizarmah.Igatha"
    
    static let SOSBeaconServiceID: CBUUID = CBUUID(
        string: "928278d6-e3d5-42f5-95f8-a6d4fa0a43aa"
    )
    
    // threshold for sudden changes in linear acceleration
    // 3.0 g ~= dropping your phone on a hard surface
    static let SensorAccelerationThreshold: Double = 3.0
    
    // threshold for sudden changes in rotation
    // 6.0 r/s ~= almost a full rotation in 1 second
    static let SensorRotationThreshold: Double = 6.0
    
    // threshold for sudden changes in atmospheric pressure
    // 0.1 kPa ~= altitude change of approx. 8 to 12 meters
    static let SensorPressureThreshold: Double = 0.1
    
    // time window for temporally correlating sensor readings
    // if all thresholds exceed in 1.5s then we have an incident
    static let IncidentTemporalCorrelationTimeWindow: Double = 1.5
    
    // percentage of how much a new value should affect the old value
    static let RSSIExponentialMovingAverageSmoothingFactor: Double = 0.18
}
