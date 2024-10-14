//
//  EmergencyManager.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import SwiftUI
import UserNotifications

class EmergencyManager: NSObject {
    static let shared = EmergencyManager()
    
    weak var delegate: EmergencyManagerDelegate?
    
    private var sosBeacon: SOSBeacon!
    private var sirenPlayer: SirenPlayer!
    
    private var incidentDetector: IncidentDetector!
    
    private var sosConfirmationTimer: Timer?
    private let sosConfirmationGracePeriod: TimeInterval =
    Constants.IncidentResponseGracePeriod
    
    public var isAvailable: Bool {
        return (
            sosBeacon.isAvailable
            && sirenPlayer.isAvailable
            && incidentDetector.isAvailable
        )
    }
    public var isSOSActive: Bool {
        return (
            sosBeacon.isActive
            || sirenPlayer.isActive
        )
    }
    
    private override init() {
        super.init()
        
        sosBeacon = SOSBeacon()
        sirenPlayer = SirenPlayer()
        
        incidentDetector = IncidentDetector(
            accelerationThreshold: Constants.SensorAccelerationThreshold,
            rotationThreshold: Constants.SensorRotationThreshold,
            pressureThreshold: Constants.SensorPressureThreshold,
            eventTimeWindow: Constants.IncidentTemporalCorrelationTimeWindow
        )
        
        sirenPlayer.delegate = self
        sosBeacon.delegate = self
        
        incidentDetector.delegate = self
        
        // setup local notifications
        setupNotificationCategories()
        UNUserNotificationCenter.current().delegate = self
        requestNotificationPermissions()
    }
    
    deinit {
        stopSOS()
        
        incidentDetector.stopDetection()
    }
    
    @objc func startSOS() {
        guard isAvailable else { return }
        
        sosBeacon.startBroadcasting()
        sirenPlayer.startSiren()
        
        delegate?.distressSignalStarted()
    }
    
    func stopSOS() {
        sirenPlayer.stopSiren()
        sosBeacon.stopBroadcasting()
        
        sosConfirmationTimer?.invalidate()
        sosConfirmationTimer = nil
        
        delegate?.distressSignalStopped()
    }
    
    // starts confirmation timer for SOS
    func startSOSConfirmationTimer() {
        sosConfirmationTimer?.invalidate()
        
        // Schedule a new timer
        sosConfirmationTimer = Timer.scheduledTimer(
            timeInterval: sosConfirmationGracePeriod,
            target: self,
            selector: #selector(startSOS),
            userInfo: nil,
            repeats: false
        )
    }
}

extension EmergencyManager: SirenPlayerDelegate {
    // called when siren starts
    func sirenStarted() {
        // do nothing
    }
    
    // called when siren stops
    func sirenStopped() {
        // do nothing
    }
    
    // called when siren availability changes
    func sirenAvailabilityUpdate(_ isAvailable: Bool) {
        delegate?.emergencyManagerAvailabilityUpdate(isAvailable)
    }
}

extension EmergencyManager: SOSBeaconDelegate {
    // called when sos beacon starts
    func beaconStarted() {
        // do nothing
    }
    
    // called when sos beacon stops
    func beaconStopped() {
        // do nothing
    }
    
    // called when sos beacon availability changes
    func beaconAvailabilityUpdate(_ isAvailable: Bool) {
        delegate?.emergencyManagerAvailabilityUpdate(isAvailable)
    }
}

extension EmergencyManager: IncidentDetectorDelegate {
    // called when an incident is detected
    func incidentDetected() {
        // handle incident detected
        delegate?.incidentDetected()
        
        // schedule a notification if app is in background
        if UIApplication.shared.applicationState != .active {
            scheduleIncidentNotification()
        }
        
        // start confirmation timer
        startSOSConfirmationTimer()
    }
    
    // called when incident detection starts
    func incidentDetectionStarted() {
        // do nothing
    }
    
    // called when incident detection stops
    func incidentDetectionStopped() {
        // do nothing
    }
    
    // called when incident detector availablility changes
    func incidentDetectorAvailabilityUpdate(
        _ isAvailable: Bool
    ) {
        if (isAvailable) {
            incidentDetector.startDetection()
        } else {
            incidentDetector.stopDetection()
        }
        
        delegate?.emergencyManagerAvailabilityUpdate(isAvailable)
    }
}

extension EmergencyManager: UNUserNotificationCenterDelegate {
    // requests notification permissions
    private func requestNotificationPermissions() {
        UNUserNotificationCenter
            .current()
            .requestAuthorization(
                options: [.alert, .sound, .badge]
            ) { granted, error in
                if let error = error {
                    print("EmergencyManager: notification permission error: \(error)")
                }
            }
    }
    
    // sets up notification categories and actions
    private func setupNotificationCategories() {
        let respondAction = UNNotificationAction(
            identifier: "RESPOND_ACTION",
            title: "I'm Okay",
            options: [.foreground]
        )
        
        let helpAction = UNNotificationAction(
            identifier: "NEED_HELP_ACTION",
            title: "Need Help",
            options: [.destructive]
        )
        
        let category = UNNotificationCategory(
            identifier: "INCIDENT_CATEGORY",
            actions: [respondAction, helpAction],
            intentIdentifiers: [],
            options: []
        )
        
        UNUserNotificationCenter.current().setNotificationCategories([category])
    }
    
    // schedules a notification when an incident is detected
    private func scheduleIncidentNotification() {
        let content = UNMutableNotificationContent()
        
        content.title = "Incident Detected"
        content.body = "Are you okay?"
        content.sound = .default
        content.categoryIdentifier = "INCIDENT_CATEGORY"
        
        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: 1,
            repeats: false
        )
        
        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: trigger
        )
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("EmergencyManager: failed to schedule notification: \(error)")
            }
        }
    }
    
    // handle notification when app is in foreground
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound])
    }
    
    // handle user response to notification
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        switch response.actionIdentifier {
        case "RESPOND_ACTION", UNNotificationDefaultActionIdentifier:
            stopSOS()
            
        case "NEED_HELP_ACTION":
            startSOS()
            
        default:
            break
        }
        
        completionHandler()
    }
}

protocol EmergencyManagerDelegate: AnyObject {
    func incidentDetected()
    
    func distressSignalStarted()
    func distressSignalStopped()
    
    func emergencyManagerAvailabilityUpdate(_ isAvailable: Bool)
}
