//
//  IncidentDetector.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation

class IncidentDetector: SensorDelegate, ObservableObject {
    private let eventTimeWindow: TimeInterval
    private var eventTimes: [SensorType: Date] = [:]
    
    private let accelerometerSensor: AccelerometerSensor
    private let gyroscopeSensor: GyroscopeSensor
    private let barometerSensor: BarometerSensor
    
    @Published var incidentDetected: Bool = false
    
    init(
        accelerationThreshold: Double,
        rotationThreshold: Double,
        pressureThreshold: Double,
        eventTimeWindow: TimeInterval
    ) {
        self.eventTimeWindow = eventTimeWindow
        
        accelerometerSensor = AccelerometerSensor(
            threshold: accelerationThreshold,
            updateInterval: 0.1
        )
        gyroscopeSensor = GyroscopeSensor(
            threshold: rotationThreshold,
            updateInterval: 0.1
        )
        barometerSensor = BarometerSensor(
            threshold: pressureThreshold
        )
        
        accelerometerSensor.delegate = self
        gyroscopeSensor.delegate = self
        barometerSensor.delegate = self
    }
    
    func startDetection() {
        accelerometerSensor.startUpdates()
        gyroscopeSensor.startUpdates()
        barometerSensor.startUpdates()
    }
    
    func stopDetection() {
        accelerometerSensor.stopUpdates()
        gyroscopeSensor.stopUpdates()
        barometerSensor.stopUpdates()
    }
    
    // called when a sensor exceeds a threshold
    func sensorDidExceedThreshold(sensorType: SensorType, eventTime: Date) {
        eventTimes[sensorType] = eventTime
        
        checkForIncident()
    }
    
    // called when an incident is suspected
    private func checkForIncident() {
        let currentTime = Date()
        
        // ensure all events have occurred
        guard eventTimes.count == 3 else { return }
        
        // ensure all events occurred within the time window
        for (_, eventTime) in eventTimes {
            if currentTime.timeIntervalSince(eventTime) > eventTimeWindow {
                // event is too old; do not consider it
                return
            }
        }
        
        // incident detected
        DispatchQueue.main.async {
            self.incidentDetected = true
        }
        
        print("IncidentDetector: incident detected")
    }
}

