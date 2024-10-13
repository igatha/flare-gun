//
//  ContentView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 05/10/2024.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var proximityScanner: ProximityScanner
    
    @StateObject private var sosBeacon: SOSBeacon
    @StateObject private var sirenPlayer: SirenPlayer
    
    @StateObject private var incidentDetector: IncidentDetector
    
    @State private var activeAlert: ActiveAlert?
    @State private var alertResponseTimer: Timer?
    
    @State private var selectedDevice: Device? = nil
    
    init() {
        let proximityScanner = ProximityScanner()
        _proximityScanner = StateObject(wrappedValue: proximityScanner)
        
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
            // list of devices
            DeviceListView(
                devices: proximityScanner.devices,
                onDeviceSelect: { device in
                    // open sheet with selected device
                    selectedDevice = device
                }
            )
            .padding(.bottom, 8)
            
            Spacer()
            
            // sos button
            Button(action: {
                if sirenPlayer.isPlaying || sosBeacon.isBroadcasting {
                    stopSOS()
                } else {
                    // show confirmation alert
                    activeAlert = .sosConfirmation
                }
            }) {
                Text(
                    sirenPlayer.isPlaying
                    && sosBeacon.isBroadcasting
                    ? "Stop SOS"
                    : "Send SOS"
                )
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(
                    sirenPlayer.isPlaying
                    && sosBeacon.isBroadcasting
                    ? Color.gray
                    : Color.red
                )
                .cornerRadius(8)
            }
            .disabled(!sosBeacon.broadcastEnabled)
            .padding([.horizontal, .bottom])
            .animation(.easeInOut, value: sirenPlayer.isPlaying && sosBeacon.isBroadcasting)
            .alert(item: $activeAlert) { alertType in
                switch alertType {
                case .sosConfirmation:
                    return Alert(
                        title: Text("Are you sure?"),
                        message: Text("This will broadcast your location and start a loud siren."),
                        primaryButton: .destructive(Text("Yes")) {
                            triggerSOS()
                        },
                        secondaryButton: .cancel()
                    )
                case .incidentDetected:
                    return Alert(
                        title: Text("Incident Detected"),
                        message: Text("Are you okay?"),
                        primaryButton: .default(Text("I'm Okay")) {
                            userResponded()
                        },
                        secondaryButton: .destructive(Text("Need Help")) {
                            triggerSOS()
                        }
                    )
                }
            }
        }
        .sheet(item: $selectedDevice) { device in
            // show the device details
            DeviceDetailView(device: device)
        }
        .onAppear {
            // start incident detection and sos discovery
            incidentDetector.startDetection()
            proximityScanner.startScanning()
        }
        .onDisappear {
            // stop all services
            sirenPlayer.stopSiren()
            sosBeacon.stopBroadcasting()
            
            proximityScanner.stopScanning()
            incidentDetector.stopDetection()
        }
        .onReceive(
            incidentDetector.$incidentDetected
        ) { detected in
            guard detected else { return }
            
            activeAlert = .incidentDetected
            startAlertResponseTimer()
            
            incidentDetector.incidentDetected = false
        }
    }
    
    private func startAlertResponseTimer() {
        // invalidate any existing timer
        alertResponseTimer?.invalidate()
        
        alertResponseTimer = Timer.scheduledTimer(
            withTimeInterval: Constants.IncidentResponseGracePeriod,
            repeats: false
        ) { _ in
            // trigger SOS if user doesn't respond in time
            triggerSOS()
        }
    }

    private func userResponded() {
        // stop the response timer if user responds
        alertResponseTimer?.invalidate()
        alertResponseTimer = nil
    }

    private func triggerSOS() {
        sosBeacon.startBroadcasting()
        sirenPlayer.startSiren()
        
        print("ContentView: started SOS")
    }
    
    private func stopSOS() {
        sosBeacon.stopBroadcasting()
        sirenPlayer.stopSiren()
        
        print("ContentView: stopped SOS")
    }
}

enum ActiveAlert: Identifiable {
    case sosConfirmation
    case incidentDetected
    
    var id: Int {
        hashValue
    }
}

#Preview {
    ContentView()
}
