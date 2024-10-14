//
//  ContentView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 05/10/2024.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = ContentViewModel()
    
    @State private var selectedDevice: Device? = nil
    
    var body: some View {
        VStack {
            // list of devices
            DeviceListView(
                devices: viewModel.devices,
                onDeviceSelect: { device in
                    // open sheet with selected device
                    selectedDevice = device
                }
            )
            .padding(.bottom, 8)
            
            Spacer()
            
            // sos button
            Button(action: {
                if viewModel.isSOSActive {
                    viewModel.stopSOS()
                } else {
                    // show confirmation alert
                    viewModel.activeAlert = .sosConfirmation
                }
            }) {
                Text(
                    viewModel.isSOSAvailable
                    ? viewModel.isSOSActive
                        ? "Stop SOS"
                        : "Send SOS"
                    : "SOS Unavailable"
                )
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(
                    viewModel.isSOSActive
                    ? Color.gray
                    : Color.red
                )
                .opacity(
                    viewModel.isSOSAvailable
                    ? 1
                    : 0.75
                )
                .cornerRadius(8)
            }
            .disabled(!viewModel.isSOSAvailable)
            .padding([.horizontal, .bottom])
            .animation(.easeInOut, value: viewModel.isSOSActive)
            .alert(item: $viewModel.activeAlert) { alertType in
                switch alertType {
                case .sosConfirmation:
                    return Alert(
                        title: Text("Are you sure?"),
                        message: Text("This will broadcast your location and start a loud siren."),
                        primaryButton: .destructive(Text("Yes")) {
                            viewModel.startSOS()
                            
                            viewModel.activeAlert = nil
                        },
                        secondaryButton: .cancel() {
                            viewModel.activeAlert = nil
                        }
                    )
                case .incidentDetected:
                    return Alert(
                        title: Text("Incident Detected"),
                        message: Text("Are you okay?"),
                        primaryButton: .default(Text("I'm Okay")) {
                            viewModel.stopSOS()
                            
                            viewModel.activeAlert = nil
                        },
                        secondaryButton: .destructive(Text("Need Help")) {
                            viewModel.startSOS()
                            
                            viewModel.activeAlert = nil
                        }
                    )
                }
            }
        }
        .sheet(item: $selectedDevice) { device in
            // show the device details
            DeviceDetailView(device: device)
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
