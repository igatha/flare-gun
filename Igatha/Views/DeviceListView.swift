//
//  DeviceListView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 13/10/2024.
//

import SwiftUI

struct DeviceListView: View {
    let devices: [Device]
    
    var onDeviceSelect: (Device) -> Void
    
    var body: some View {
        List {
            Section {
                if sortedDevices.isEmpty {
                    Text(
                        "No devices found nearby."
                    )
                    .foregroundColor(.gray)
                    .padding()
                } else {
                    ForEach(sortedDevices) { device in
                        HStack(spacing: 16) {
                            // device icon
                            Image(systemName: "person.circle.fill")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 40, height: 40)
                                .foregroundColor(
                                    getColor(for: device.estimateDistance())
                                )
                            
                            VStack(alignment: .leading, spacing: 4) {
                                // device short name
                                Text(device.shortName)
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                // device distance
                                Text(
                                    "\(String(format: "%.2f", device.estimateDistance())) meters away"
                                )
                                .font(.subheadline)
                                .foregroundColor(.primary)
                            }
                            
                            Spacer()
                        }
                        .padding(.vertical, 4)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            // trigger the closure when tapped
                            onDeviceSelect(device)
                        }
                    }
                }
            } header: {
                Text("People Seeking Help")
            }
        }
        .listStyle(.automatic)
    }
    
    var sortedDevices: [Device] {
        devices.sorted { $0.estimateDistance() < $1.estimateDistance() }
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
