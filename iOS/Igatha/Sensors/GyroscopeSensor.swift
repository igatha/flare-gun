//
//  GyroscopeSensor.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation
import CoreMotion

class GyroscopeSensor: Sensor {
    weak var delegate: SensorDelegate?
    
    typealias T = CMMotionManager
    internal let sensor: CMMotionManager
    
    internal let threshold: Double
    private let updateInterval: TimeInterval
    
    public var isAvailable: Bool {
        return sensor.isGyroAvailable
    }
    
    init(
        threshold: Double,
        updateInterval: TimeInterval
    ) {
        self.threshold = threshold
        self.updateInterval = updateInterval
        
        sensor = CMMotionManager()
        sensor.gyroUpdateInterval = updateInterval
    }
    
    func startUpdates() {
        guard isAvailable else { return }
        
        sensor.startGyroUpdates(
            to: .main
        ) { [weak self] data, error in
            guard
                let self = self,
                let data = data
            else { return }
            
            let rotationRate = data.rotationRate
            
            let totalRotationRate = sqrt(
                rotationRate.x * rotationRate.x +
                rotationRate.y * rotationRate.y +
                rotationRate.z * rotationRate.z
            )
            
            guard totalRotationRate > self.threshold else { return }
            
            self.delegate?.sensorExceededThreshold(
                sensorType: .gyroscope,
                eventTime: Date()
            )
        }
    }
    
    func stopUpdates() {
        sensor.stopGyroUpdates()
    }
}

