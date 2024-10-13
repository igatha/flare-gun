//
//  DeviceDetailView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 13/10/2024.
//

import SwiftUI

struct DeviceDetailView: View {
    let device: Device
    
    // environment mode to dismiss the sheet
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        // wrap in navigation view to add a close button
        NavigationView {
            VStack(
                spacing: 20
            ) {
                // device icon
                Image(systemName: "person.circle.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 100, height: 100)
                    .foregroundColor(
                        getColor(for: device.estimateDistance())
                    )
                    .padding()
                
                // device short name
                Text(device.shortName)
                    .font(.title)
                    .fontWeight(.bold)
                
                // device distance
                Text(
                    "\(String(format: "%.2f", device.estimateDistance())) meters away"
                )
                .font(.headline)
                .foregroundColor(.primary)
                
                Spacer()
            }
            .padding()
            .navigationBarTitle(
                "Device Details",
                displayMode: .inline
            )
            .navigationBarItems(
                trailing: Button("Close") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
    
    func getColor(for distance: Double) -> Color {
        switch distance {
        case 0...10:
            return .green
        case 11...20:
            return .yellow
        case 21...50:
            return .orange
        default:
            return .red
        }
    }
}
