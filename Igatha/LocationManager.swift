//
//  LocationManager.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 11/10/2024.
//

import Foundation
import CoreLocation

class LocationManager: NSObject, ObservableObject {
    // public properties to access the coordinates
    @Published public private(set) var latitude: CLLocationDegrees = 0
    @Published public private(set) var longitude: CLLocationDegrees = 0
    @Published public private(set) var altitude: CLLocationDistance = 0
    
    @Published public private(set) var locationEnabled = false
    
    private var locationManager: CLLocationManager!
    
    override init() {
        super.init()
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        
        locationManager.requestAlwaysAuthorization()
    }
}

extension LocationManager: CLLocationManagerDelegate {
    // called when location authorization changes
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        print("LocationManager: requesting permissions")
        
        switch manager.authorizationStatus {
        case .notDetermined:
            print("LocationManager: notDetermined")
            manager.requestAlwaysAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            print("LocationManager: authorized")
            manager.requestLocation()
            manager.startUpdatingLocation()
        case .restricted, .denied:
            // handle denied permissions (e.g., notify the user)
            // TODO: Handle cases
            print("LocationManager: restricted | denied")
        @unknown default:
            print("LocationManager: unknown")
        }
    }
    
    // called when there are location updates
    func locationManager(
        _ manager: CLLocationManager,
        didUpdateLocations locations: [CLLocation]
    ) {
        guard let location = locations.last else {
            print("LocationManager: no valid location")
            return
        }
        
        // update latitude and longitude
        latitude = location.coordinate.latitude
        longitude = location.coordinate.longitude
        altitude = location.altitude
        
        locationEnabled = true
        
        print("LocationManager: coords: \(latitude), \(longitude)")
    }
    
    // called with location manager errors
    func locationManager(
        _ manager: CLLocationManager,
        didFailWithError error: Error
    ) {
        print("LocationManager: error: \(error.localizedDescription)")
    }
}
