//
//  LocationManager.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 17/10/2024.
//

import Foundation
import CoreLocation

class LocationManager: NSObject {
    weak var delegate: LocationManagerDelegate?
    
    private var locationManager: CLLocationManager!
    
    public var isAvailable: Bool = false
    public var isActive: Bool = false
    
    override init() {
        super.init()
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        
        // background updates to read core motion sensors
        locationManager.allowsBackgroundLocationUpdates = true
        
        // ensure the location updates arent paused by system
        locationManager.pausesLocationUpdatesAutomatically = false
        
        // uses radio signals; low battery consumption
        locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
        
        locationManager.requestAlwaysAuthorization()
    }
    
    deinit {
        stopUpdates()
    }
    
    func startUpdates() {
        guard
            isAvailable
                && !isActive
        else { return }
        
        locationManager.startUpdatingLocation()
        
        NSLog("LocationManager: started updates")
        
        delegate?.locationUpdatesStarted()
    }
    
    func stopUpdates() {
        locationManager.stopUpdatingLocation()
        
        NSLog("LocationManager: stopped updates")
        
        delegate?.locationUpdatesStopped()
    }
}

extension LocationManager: CLLocationManagerDelegate {
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        switch manager.authorizationStatus {
        case .notDetermined:
            locationManager.requestAlwaysAuthorization()
            return
            
        case .authorizedAlways:
            isAvailable = true
            
        case .authorizedWhenInUse, .restricted, .denied:
            isAvailable = false
            
        @unknown default:
            isAvailable = false
        }
        
        NSLog("LocationManager:\(isAvailable ? "" : " not") available")
        
        delegate?.locationManagerAvailabilityUpdate(isAvailable)
        
        if !isAvailable {
            stopUpdates()
        }
    }
    
    func locationManager(
        _ manager: CLLocationManager,
        didFailWithError error: Error
    ) {
        isAvailable = false
    }
    
    func locationManagerDidResumeLocationUpdates(_ manager: CLLocationManager) {
        isActive = true
    }
    
    func locationManagerDidPauseLocationUpdates(_ manager: CLLocationManager) {
        isActive = false
    }
}

protocol LocationManagerDelegate: AnyObject {
    func locationUpdatesStarted()
    func locationUpdatesStopped()
    
    func locationManagerAvailabilityUpdate(_ isAvailable: Bool)
}
