//
//  BarometerSensor.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation
import CoreMotion

class BarometerSensor {
    weak var delegate: SensorDelegate?
    
    private let altimeter = CMAltimeter()
    
    private let threshold: Double
    private var initialPressure: Double?
    
    init(
        threshold: Double
    ) {
        self.threshold = threshold
    }
    
    func startUpdates() {
        guard CMAltimeter.isRelativeAltitudeAvailable() else { return }
        
        altimeter.startRelativeAltitudeUpdates(
            to: .main
        ) { [weak self] data, error in
            guard
                let self = self,
                let data = data
            else { return }
            
            let pressure = data.pressure.doubleValue // in kPa
            if self.initialPressure == nil {
                self.initialPressure = pressure
                return
            }
            
            guard let initialPressure = self.initialPressure else { return }
            
            let pressureChange = abs(pressure - initialPressure)
            
            guard pressureChange > self.threshold else { return }
            
            self.delegate?.sensorDidExceedThreshold(
                sensorType: .barometer,
                eventTime: Date()
            )
        }
    }
    
    func stopUpdates() {
        altimeter.stopRelativeAltitudeUpdates()
    }
}

