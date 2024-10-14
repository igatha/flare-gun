//
//  ViewModel.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import Foundation

class ContentViewModel: ObservableObject {
    @Published public var isSOSAvailable: Bool = false
    @Published public var isSOSActive: Bool = false
    
    @Published public var activeAlert: AlertType? = nil
    
    @Published private var devicesMap: [String: Device] = [:]
    public var devices: [Device] {
        return Array(devicesMap.values)
    }
    
    private let emergencyManager: EmergencyManager
    private let proximityScanner: ProximityScanner
    
    init() {
        emergencyManager = EmergencyManager.shared
        proximityScanner = ProximityScanner()
        
        emergencyManager.delegate = self
        proximityScanner.delegate = self
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
}

extension ContentViewModel: EmergencyManagerDelegate {
    func incidentDetected() {
        DispatchQueue.main.async {
            self.activeAlert = .incidentDetected
        }
    }
    
    func distressSignalStarted() {
        DispatchQueue.main.async {
            self.isSOSActive = true
        }
    }
    
    func distressSignalStopped() {
        DispatchQueue.main.async {
            self.isSOSActive = false
        }
    }
    
    func emergencyManagerAvailabilityUpdate(_ isAvailable: Bool) {
        DispatchQueue.main.async {
            self.isSOSAvailable = isAvailable
        }
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
    case incidentDetected
    
    var id: Int {
        switch self {
        case .sosConfirmation:
            return 1
        case .incidentDetected:
            return 2
        }
    }
}
