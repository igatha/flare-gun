//
//  ViewModel.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import SwiftUI

class ContentViewModel: ObservableObject {
    @Published public var isSOSAvailable: Bool = false
    @Published public var isSOSActive: Bool = false
    
    @Published public var activeAlert: AlertType? = nil
    
    @Published private var devicesMap: [String: Device] = [:]
    public var devices: [Device] {
        return devicesMap.values.sorted {
            $0.rssi > $1.rssi
        }
    }
    
    private let emergencyManager: EmergencyManager
    private let proximityScanner: ProximityScanner
    
    init() {
        emergencyManager = EmergencyManager.shared
        proximityScanner = ProximityScanner()
        
        emergencyManager.delegate = self
        proximityScanner.delegate = self
        
        updateSOSAvailability()
    }
    
    deinit {
        proximityScanner.stopScanning()
    }
    
    func startSOS() {
        emergencyManager.startSOS()
    }
    
    func stopSOS() {
        emergencyManager.stopSOS()
    }
    
    func updateSOSAvailability(
        isAvailable: Bool? = nil,
        isActive: Bool? = nil
    ) {
        DispatchQueue.main.async {
            self.isSOSAvailable = isAvailable ?? self.emergencyManager.isAvailable
            self.isSOSActive = isActive ?? self.emergencyManager.isSOSActive
        }
    }
}

extension ContentViewModel: EmergencyManagerDelegate {
    func disasterDetected() {
        DispatchQueue.main.async {
            self.activeAlert = .disasterDetected
        }
    }
    
    func distressSignalStarted() {
        updateSOSAvailability(isActive: true)
    }
    
    func distressSignalStopped() {
        updateSOSAvailability(isActive: false)
    }
    
    func emergencyManagerAvailabilityUpdate(_ isAvailable: Bool) {
        updateSOSAvailability(isAvailable: isAvailable)
    }
}

extension ContentViewModel: ProximityScannerDelegate {
    func scannedDevice(_ device: Device) {
        DispatchQueue.main.async {
            if !self.devicesMap.keys.contains(device.id) {
                self.devicesMap[device.id] = device
            }
            
            self.devicesMap[device.id]?.update(rssi: device.rssi)
        }
    }
    
    func scannerAvailabilityUpdate(_ isAvailable: Bool) {
        guard isAvailable else {
            proximityScanner.stopScanning()
            return
        }
        
        proximityScanner.startScanning()
    }
}

enum AlertType: Identifiable {
    case sosConfirmation
    case disasterDetected
    
    var id: Int {
        switch self {
        case .sosConfirmation:
            return 1
        case .disasterDetected:
            return 2
        }
    }
}
