//
//  SettingsView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var viewModel: ContentViewModel // Access the viewModel
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    Toggle(isOn: $viewModel.backgroundMonitoringEnabled) {
                        Text("Always-On Monitoring")
                    }
                    .onChange(of: viewModel.backgroundMonitoringEnabled) {
                        if viewModel.backgroundMonitoringEnabled {
                            viewModel.enableBackgroundMonitoring()
                        } else {
                            viewModel.disableBackgroundMonitoring()
                        }
                    }
                    
                    Text("Enabling always-on monitoring allows Igatha to detect emergencies even when the app is not in use. This may increase battery consumption.")
                        .font(.caption)
                        .foregroundColor(.gray)
                } header: {
                    Text("Background Services")
                        .padding(.vertical)
                }
            }
            .navigationBarTitle("Settings", displayMode: .inline)
            .navigationBarItems(trailing: Button("Done") {
                presentationMode.wrappedValue.dismiss()
            })
        }
    }
}
