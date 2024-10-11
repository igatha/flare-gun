//
//  ContentView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 05/10/2024.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var sosBeacon: SOSBeacon
    
    init() {
        let sosBeacon = SOSBeacon()
        _sosBeacon = StateObject(wrappedValue: sosBeacon)
    }
    
    var body: some View {
        VStack {
            Text("Hello, world!")
                .padding()
            
            Text("Broadcast enabled: \(sosBeacon.broadcastEnabled)")
            
            // start broadcasting button
            Button(action: {
                // start broadcasting
                sosBeacon.startBroadcasting()
            }) {
                Text("SOS")
                    .padding()
                    .background(
                        sosBeacon.broadcastEnabled
                        ? Color.red
                        : Color.gray
                    )
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(
                !sosBeacon.broadcastEnabled
            )
            .padding()
            
        }
    }
}

#Preview {
    ContentView()
}
