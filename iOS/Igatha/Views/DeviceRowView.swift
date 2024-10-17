//
//  DeviceRowView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 13/10/2024.
//

import SwiftUI

struct DeviceRowView: View {
    @ObservedObject var device: Device
    
    private var isStale: Bool {
        let fiveMinutesAgo = Date().addingTimeInterval(-300)
        return device.lastSeen < fiveMinutesAgo
    }
    
    var body: some View {
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
