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
                    // stop SOS
                    sosBeacon.stopBroadcasting()
                    sirenPlayer.stopSiren()
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
                            print("User confirmed SOS")
                            // start broadcasting and siren
                            sosBeacon.startBroadcasting()
                            sirenPlayer.startSiren()
                        },
                        secondaryButton: .cancel {
                            print("User canceled SOS")
                        }
                    )
                case .incidentDetected:
                    return Alert(
                        title: Text("Incident Detected"),
                        message: Text("Are you okay?"),
                        primaryButton: .default(Text("I'm Okay")) {
                            print("User is okay")
                            // User is okay, no action needed
                        },
                        secondaryButton: .destructive(Text("Need Help")) {
                            print("User needs help, starting SOS")
                            // Start emergency services
                            sosBeacon.startBroadcasting()
                            sirenPlayer.startSiren()
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
            
            // TODO: proximityScanner.startScanning()
        }
        .onDisappear {
            // stop all services
            sirenPlayer.stopSiren()
            sosBeacon.stopBroadcasting()
            
            // TODO: proximityScanner.stopScanning()
            
            incidentDetector.stopDetection()
        }
        .onReceive(
            incidentDetector.$incidentDetected
        ) { detected in
            guard detected else { return }
            
            activeAlert = .incidentDetected
            incidentDetector.incidentDetected = false
        }
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
