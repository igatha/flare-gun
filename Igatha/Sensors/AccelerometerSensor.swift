//
//  AccelerometerSensor.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation
import CoreMotion

class AccelerometerSensor {
    weak var delegate: SensorDelegate?
    
    private let motionManager = CMMotionManager()
    
    private let threshold: Double
    private let updateInterval: TimeInterval
    
    init(
        threshold: Double,
        updateInterval: TimeInterval
    ) {
        self.threshold = threshold
        self.updateInterval = updateInterval
    }
    
    func startUpdates() {
        guard motionManager.isAccelerometerAvailable else { return }
        
        motionManager.accelerometerUpdateInterval = threshold
        
        motionManager.startAccelerometerUpdates(
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
            
            self.delegate?.sensorDidExceedThreshold(
                sensorType: .accelerometer,
                eventTime: Date()
            )
        }
    }
    
    func stopUpdates() {
        motionManager.stopAccelerometerUpdates()
    }
}

