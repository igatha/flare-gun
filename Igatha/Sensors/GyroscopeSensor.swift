//
//  GyroscopeSensor.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation
import CoreMotion

class GyroscopeSensor {
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
        guard motionManager.isGyroAvailable else {
            print("GyroscopeSensor: not available")
            return
        }
        
        motionManager.gyroUpdateInterval = updateInterval
        
        motionManager.startGyroUpdates(
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
            
            self.delegate?.sensorDidExceedThreshold(
                sensorType: .gyroscope,
                eventTime: Date()
            )
        }
        
        print("GyroscopeSensor: started sensor")
    }
    
    func stopUpdates() {
        motionManager.stopGyroUpdates()
        
        print("GyroscopeSensor: stopped sensor")
    }
}

