//
//  ContentView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 05/10/2024.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var sosBeacon: SOSBeacon
    @StateObject private var sirenPlayer: SirenPlayer
    
    @StateObject private var incidentDetector: IncidentDetector
    
    @State private var showingAlert = false
    
    @State private var isAlerting = false
    
    init() {
        let sosBeacon = SOSBeacon()
        _sosBeacon = StateObject(wrappedValue: sosBeacon)
        
        let sirenPlayer = SirenPlayer()
        _sirenPlayer = StateObject(wrappedValue: sirenPlayer)
        
        let incidentDetector = IncidentDetector(
            accelerationThreshold: Constants.SensorAccelerationThreshold,
            rotationThreshold: Constants.SensorRotationThreshold,
            pressureThreshold: Constants.SensorPressureThreshold,
            eventTimeWindow: Constants.IncidentTemporalCorrelationTimeWindow
        )
        _incidentDetector = StateObject(wrappedValue: incidentDetector)
    }
    
    var body: some View {
        VStack {
            Text("Hello, world!")
                .padding()
            
            Text("Broadcast enabled: \(sosBeacon.broadcastEnabled.description)")
            
            Text("Incident detected: \(incidentDetector.incidentDetected.description)")
            
            // Start broadcasting button
            Button(action: {
                if isAlerting {
                    sosBeacon.stopBroadcasting()
                    sirenPlayer.stopSiren()
                    isAlerting = false
                } else {
                    // Start broadcasting
                    sosBeacon.startBroadcasting()
                    sirenPlayer.startSiren()
                    isAlerting = true
                }
            }) {
                Text("SOS")
                    .padding()
                    .background(
                        sosBeacon.broadcastEnabled
                        ? Color.red
                        : Color.gray
                    )
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(
                !sosBeacon.broadcastEnabled
            )
            .padding()
        }
        .onAppear {
            incidentDetector.startDetection()
        }
        .onDisappear {
            incidentDetector.stopDetection()
            
            sosBeacon.stopBroadcasting()
            sirenPlayer.stopSiren()
        }
        .onReceive(incidentDetector.$incidentDetected) { detected in
            if detected {
                showingAlert = true
            }
        }
        .alert(isPresented: $showingAlert) {
            Alert(
                title: Text("Incident Detected"),
                message: Text("Are you okay?"),
                primaryButton: .default(Text("I'm Okay")) {
                    // User is okay, no action needed
                },
                secondaryButton: .destructive(Text("Need Help")) {
                    // start emergency services
                    sosBeacon.startBroadcasting()
                    sirenPlayer.startSiren()
                    // optionally other actions
                }
            )
        }
    }
}

#Preview {
    ContentView()
}
