//
//  BarometerSensor.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation
import CoreMotion

class BarometerSensor: Sensor {
    weak var delegate: SensorDelegate?
    
    typealias T = CMAltimeter
    internal let sensor: CMAltimeter
    
    internal let threshold: Double    
    private var initialPressure: Double?
    
    public var isAvailable: Bool {
        return CMAltimeter.isRelativeAltitudeAvailable()
    }
    
    init(
        threshold: Double
    ) {
        self.threshold = threshold
        
        sensor = CMAltimeter()
    }
    
    func startUpdates() {
        guard isAvailable else { return }
        
        sensor.startRelativeAltitudeUpdates(
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
            
            self.delegate?.sensorExceededThreshold(
                sensorType: .barometer,
                eventTime: Date()
            )
        }
        
        NSLog("BarometerSensor: started updates")
    }
    
    func stopUpdates() {
        sensor.stopRelativeAltitudeUpdates()
        
        NSLog("BarometerSensor: stopped updates")
    }
}

