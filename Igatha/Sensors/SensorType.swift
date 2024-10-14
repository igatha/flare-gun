//
//  SensorType.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation

enum SensorType {
    case accelerometer
    case gyroscope
    case barometer
}

public protocol AnySensor: AnyObject {
    var isAvailable: Bool { get }
    
    func startUpdates()
    func stopUpdates()
}

protocol Sensor: AnySensor {
    var delegate: SensorDelegate? { get }
    
    associatedtype T
    var sensor: T { get }
    
    var threshold: Double { get }
}

protocol SensorDelegate: AnyObject {
    func sensorExceededThreshold(sensorType: SensorType, eventTime: Date)
}
