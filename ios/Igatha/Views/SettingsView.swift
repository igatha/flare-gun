//
//  SettingsView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import SwiftUI

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    
    var body: some View {
        Form {
            // Background services.
            Section {
                // Disaster detection.
                Toggle(isOn: $viewModel.disasterDetectionEnabled) {
                    Text("Disaster Detection")
                    
                    Text("Detects disasters and sends SOS when the app is not in use. This requires location permission. This may increase battery consumption.")
                        .font(.caption)
                        .foregroundColor(.gray)
                }
            } header: {
                Text("Background Services")
                    .padding(.vertical, 4)
            } footer: {
                Text("Services might require additional permissions.")
                    .padding(.vertical, 4)
            }
            
            // Feedback.
            Section {
                FeedbackRowView()
                // removes the section padding around the feedback row
                    .listRowInsets(EdgeInsets())
            } header: {
                Text("Feedback")
                    .padding(.vertical, 4)
            } footer: {
                Text("Your feedback helps us improve Igatha, for everyone.")
                    .padding(.vertical, 4)
            }
        }
        .navigationTitle("Settings")
    }
}

#Preview {
    SettingsView()
}
