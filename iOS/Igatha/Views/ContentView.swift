//
//  ContentView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 05/10/2024.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = ContentViewModel()
    
    @State private var showingSettings: Bool = false
    
    var body: some View {
        NavigationView {
            VStack {
                // list of devices
                DeviceListView(
                    devices: viewModel.devices
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
            }
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                trailing: NavigationLink(
                    destination: SettingsView()
                ) {
                    Image(systemName: "gearshape")
                        .imageScale(.large)
                }
            )
        }
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
            case .disasterDetected:
                return Alert(
                    title: Text("Disaster Detected"),
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
        .navigationBarTitleDisplayMode(.inline)
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

#Preview {
    ContentView()
}
