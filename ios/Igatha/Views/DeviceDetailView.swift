//
//  DeviceDetailView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 13/10/2024.
//

import SwiftUI

struct DeviceDetailView: View {
    @ObservedObject var device: Device
    
    private var timeSinceLastSeen: String {
        let interval = Date().timeIntervalSince(device.lastSeen)
        
        let minute = 60.0
        let hour = 60.0 * minute
        let day = 24.0 * hour
        let week = 7.0 * day
        
        if interval < minute {
            let seconds = Int(interval)
            return "\(seconds) second\(seconds != 1 ? "s" : "") ago"
        } else if interval < hour {
            let minutes = Int(interval / minute)
            return "\(minutes) minute\(minutes != 1 ? "s" : "") ago"
        } else if interval < day {
            let hours = Int(interval / hour)
            return "\(hours) hour\(hours != 1 ? "s" : "") ago"
        } else if interval < week {
            let days = Int(interval / day)
            return "\(days) day\(days != 1 ? "s" : "") ago"
        } else {
            let weeks = Int(interval / week)
            return "\(weeks) week\(weeks != 1 ? "s" : "") ago"
        }
    }
    
    var body: some View {
        TimelineView(.periodic(from: Date(), by: 60)) { context in
            let currentDate = context.date
            let interval = currentDate.timeIntervalSince(device.lastSeen)
            
            let minute = 60.0
            let hour = 60.0 * minute
            let day = 24.0 * hour
            let week = 7.0 * day
            
            var timeSinceLastSeen: String {
                if interval < minute {
                    let seconds = Int(interval)
                    return "\(seconds) second\(seconds != 1 ? "s" : "") ago"
                } else if interval < hour {
                    let minutes = Int(interval / minute)
                    return "\(minutes) minute\(minutes != 1 ? "s" : "") ago"
                } else if interval < day {
                    let hours = Int(interval / hour)
                    return "\(hours) hour\(hours != 1 ? "s" : "") ago"
                } else if interval < week {
                    let days = Int(interval / day)
                    return "\(days) day\(days != 1 ? "s" : "") ago"
                } else {
                    let weeks = Int(interval / week)
                    return "\(weeks) week\(weeks != 1 ? "s" : "") ago"
                }
            }
            
            Form {
                Section {
                    HStack {
                        Text("Name")
                        
                        Spacer()
                        
                        Text(device.shortName)
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("ID")
                        
                        Spacer()
                        
                        Text(device.id)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.trailing)
                            .font(
                                .system(
                                    .subheadline,
                                    design: .monospaced
                                )
                            )
                    }
                } header: {
                    Text("Identity")
                        .padding(.vertical, 4)
                } footer: {
                    Text("Identity is pseudonymized for privacy.")
                        .padding(.vertical, 4)
                }
                
                Section {
                    HStack {
                        Text("Distance")
                        
                        Spacer()
                        
                        Text("\(String(format: "%.1f", device.estimateDistance())) meters")
                            .font(
                                .system(
                                    .subheadline,
                                    design: .monospaced
                                )
                            )
                            .foregroundColor(.secondary)
                    }
                } header: {
                    Text("Location")
                        .padding(.vertical, 4)
                } footer: {
                    Text("Location is limited by used tech. Direction is not available. Distance is approximate and varies due to signal fluctuations. It is for general guidance only.")
                        .padding(.vertical, 4)
                }
                
                Section {
                    HStack {
                        Text("Last Seen")
                        
                        Spacer()
                        
                        Text(timeSinceLastSeen)
                            .foregroundColor(.secondary)
                    }
                } header: {
                    Text("Status")
                        .padding(.vertical, 4)
                } footer: {
                    Text("Status shows if the device is active and in range.")
                        .padding(.vertical, 4)
                }
            }
            .navigationTitle("Device Details")
        }
    }
}

struct DeviceDetailView_Previews: PreviewProvider {
    static var previews: some View {
        let mockDevice = Device(
            id: UUID(),
            rssi: -40
        )
        
        return DeviceDetailView(
            device: mockDevice
        )
        .previewLayout(
            .sizeThatFits
        )
    }
}
