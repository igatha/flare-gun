//
//  ContentView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 05/10/2024.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var locationManager: LocationManager
    
    @StateObject private var locationBroadcaster: LocationBroadcaster
    
    init() {
        let locationManager = LocationManager()
        _locationManager = StateObject(wrappedValue: locationManager)
        
        let locationBroadcaster = LocationBroadcaster(
            with: locationManager
        )
        _locationBroadcaster = StateObject(wrappedValue: locationBroadcaster)
    }
    
    var isButtonEnabled: Bool {
        return (
            locationManager.locationEnabled &&
            locationBroadcaster.broadcastEnabled
        )
    }
    
    var body: some View {
        VStack {
            Text("Hello, world!")
                .padding()
            
            Text("Latitude: \(locationManager.latitude)")
            Text("Longitude: \(locationManager.longitude)")
            Text("Altitude: \(locationManager.altitude)")
            
            Text("Location enabled: \(locationManager.locationEnabled)")
            Text("Broadcast enabled: \(locationBroadcaster.broadcastEnabled)")
            
            // start broadcasting button
            Button(action: {
                // start broadcasting
                locationBroadcaster.startBroadcasting()
            }) {
                Text("SOS")
                    .padding()
                    .background(
                        isButtonEnabled
                        ? Color.red
                        : Color.gray
                    )
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(
                !isButtonEnabled
            )
            .padding()
            
        }
    }
}

#Preview {
    ContentView()
}
