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
            
            Spacer()
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
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
