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

protocol SensorDelegate: AnyObject {
    func sensorDidExceedThreshold(sensorType: SensorType, eventTime: Date)
}
