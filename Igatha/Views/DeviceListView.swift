//
//  DeviceListView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 13/10/2024.
//

import SwiftUI

struct DeviceListView: View {
    let devices: [String: Device]
    
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
                        DeviceRowView(device: device)
                            .onTapGesture {
                                // trigger the closure when tapped
                                onDeviceSelect(device)
                            }
                    }
                }
            } header: {
                Text("People Seeking Help")
                    .padding(.vertical)
            } footer: {
                Text("Note: Distance is approximate and varies due to signal fluctuations. It is for general guidance only.")
                    .padding(.vertical)
            }
        }
        .listStyle(.automatic)
    }
    
    var sortedDevices: [Device] {
        devices.values.sorted { $0.estimateDistance() < $1.estimateDistance() }
    }
}

struct DeviceListView_Previews: PreviewProvider {
    static var previews: some View {
        // Creating mock devices
        let mockDevices = [
            Device(
                id: UUID(),
                rssi: -40
            ),
            Device(
                id: UUID(),
                rssi: -60
            ),
            Device(
                id: UUID(),
                rssi: -75
            ),
            Device(
                id: UUID(),
                rssi: -85
            )
        ]
        
        return DeviceListView(
            devices: [
                mockDevices[0].id: mockDevices[0],
                mockDevices[1].id: mockDevices[1],
                mockDevices[2].id: mockDevices[2],
                mockDevices[3].id: mockDevices[3],
            ],
            onDeviceSelect: {_ in }
        )
    }
}
