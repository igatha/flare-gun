//
//  AccelerometerSensor.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation
import CoreMotion

class AccelerometerSensor: Sensor {
    weak var delegate: SensorDelegate?
    
    typealias T = CMMotionManager
    internal let sensor: CMMotionManager
    
    internal let threshold: Double
    private let updateInterval: TimeInterval
    
    public var isAvailable: Bool {
        return sensor.isAccelerometerAvailable
    }
    
    init(
        threshold: Double,
        updateInterval: TimeInterval
    ) {
        self.threshold = threshold
        self.updateInterval = updateInterval
        
        sensor = CMMotionManager()
        sensor.accelerometerUpdateInterval = threshold
    }
    
    func startUpdates() {
        guard isAvailable else { return }
        
        sensor.startAccelerometerUpdates(
            to: .main
        ) { [weak self] data, error in
            guard
                let self = self,
                let data = data
            else { return }
            
            let acceleration = data.acceleration
            
            let totalAcceleration = sqrt(
                acceleration.x * acceleration.x +
                acceleration.y * acceleration.y +
                acceleration.z * acceleration.z
            )
            
            guard totalAcceleration > self.threshold else { return }
            
            self.delegate?.sensorExceededThreshold(
                sensorType: .accelerometer,
                eventTime: Date()
            )
        }
        
        NSLog("AccelerometerSensor: started updates")
    }
    
    func stopUpdates() {
        sensor.stopAccelerometerUpdates()
        
        NSLog("AccelerometerSensor: stopped updates")
    }
}

