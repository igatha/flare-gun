//
//  DeviceRowView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 13/10/2024.
//

import SwiftUI

struct DeviceRowView: View {
    @ObservedObject var device: Device
    
    var body: some View {
        TimelineView(.periodic(from: Date(), by: 30)) { context in
            let currentDate = context.date
            let isStale = device.lastSeen < currentDate.addingTimeInterval(-300)
            
            HStack(spacing: 16) {
                // device icon
                Image(systemName: "person.circle.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 40, height: 40)
                    .foregroundColor(.secondary)
                
                VStack(alignment: .leading, spacing: 4) {
                    // device short name
                    Text(device.shortName)
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    // device distance
                    Text(
                        "\(String(format: "%.1f", device.estimateDistance())) meters away"
                    )
                    .font(
                        .system(
                            .subheadline,
                            design: .monospaced
                        )
                    )
                    .foregroundColor(.primary)
                }
            }
            .padding(.vertical, 4)
            .contentShape(Rectangle())
            .opacity(isStale ? 0.4 : 1.0)
            .animation(.easeInOut, value: isStale)
        }
    }
}

struct DeviceRowView_Previews: PreviewProvider {
    static var previews: some View {
        // Creating mock device
        let mockDevice = Device(
            id: UUID(),
            rssi: -40
        )
        
        return DeviceRowView(
            device: mockDevice
        )
    }
}
