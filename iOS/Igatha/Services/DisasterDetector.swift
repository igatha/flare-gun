//
//  DisasterDetector.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 12/10/2024.
//

import Foundation

class DisasterDetector {
    weak var delegate: DisasterDetectorDelegate?
    
    private let eventTimeWindow: TimeInterval
    private var eventTimes: [SensorType: Date] = [:]
    
    private let locationManager: LocationManager
    
    private let accelerometerSensor: AccelerometerSensor
    private let gyroscopeSensor: GyroscopeSensor
    private let barometerSensor: BarometerSensor
    
    public var isAvailable: Bool {
        let isEnabled = UserDefaults.standard.bool(
            forKey: Constants.DisasterDetectionSettingsKey
        )
        
        return (
            isEnabled &&
            locationManager.isAvailable &&
            accelerometerSensor.isAvailable &&
            gyroscopeSensor.isAvailable &&
            barometerSensor.isAvailable
        )
    }
    public var isActive: Bool {
        return locationManager.isActive
    }
    
    init(
        accelerationThreshold: Double,
        rotationThreshold: Double,
        pressureThreshold: Double,
        eventTimeWindow: TimeInterval
    ) {
        self.eventTimeWindow = eventTimeWindow
        
        locationManager = LocationManager()
        
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
        
        locationManager.delegate = self
        
        accelerometerSensor.delegate = self
        gyroscopeSensor.delegate = self
        barometerSensor.delegate = self
        
        delegate?.disasterDetectorAvailabilityUpdate(isAvailable)
    }
    
    deinit {
        stopDetection()
    }
    
    func startDetection() {
        guard
            isAvailable
                && !isActive
        else { return }
        
        locationManager.startUpdates()
        
        NSLog("DisasterDetector: started detection")
    }
    
    func stopDetection() {
        locationManager.stopUpdates()
        
        NSLog("DisasterDetector: stopped detection")
    }
}

extension DisasterDetector: LocationManagerDelegate {
    func locationUpdatesStarted() {
        guard
            isAvailable
                && !isActive
        else { return }
        
        accelerometerSensor.startUpdates()
        gyroscopeSensor.startUpdates()
        barometerSensor.startUpdates()
        
        delegate?.disasterDetectionStarted()
    }
    
    func locationUpdatesStopped() {
        accelerometerSensor.stopUpdates()
        gyroscopeSensor.stopUpdates()
        barometerSensor.stopUpdates()
        
        delegate?.disasterDetectionStopped()
    }
    
    func locationManagerAvailabilityUpdate(_ isAvailable: Bool) {
        delegate?.disasterDetectorAvailabilityUpdate(self.isAvailable)
        
        if !isAvailable {
            stopDetection()
        }
    }
}

extension DisasterDetector: SensorDelegate {
    // called when a sensor exceeds a threshold
    func sensorExceededThreshold(
        sensorType: SensorType,
        eventTime: Date
    ) {
        eventTimes[sensorType] = eventTime
        
        checkForIncident()
        
        NSLog("DisasterDetector: \(sensorType) exceeded threshold")
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
        
        NSLog("DisasterDetector: incident detected")
        
        // incident detected
        delegate?.disasterDetected()
    }
}

protocol DisasterDetectorDelegate: AnyObject {
    func disasterDetected()
    
    func disasterDetectionStarted()
    func disasterDetectionStopped()
    
    func disasterDetectorAvailabilityUpdate(_ isAvailable: Bool)
}
