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
    
    private var disasterDetector: DisasterDetector!
    
    public var isDetectorAvailable: Bool {
        return disasterDetector.isAvailable
    }
    public var isDetectorActive: Bool {
        return disasterDetector.isActive
    }
    
    private var sosBeacon: SOSBeacon!
    private var sirenPlayer: SirenPlayer!
    
    public var isSOSAvailable: Bool {
        return (
            sosBeacon.isAvailable
            && sirenPlayer.isAvailable
        )
    }
    public var isSOSActive: Bool {
        return (
            sosBeacon.isActive
            || sirenPlayer.isActive
        )
    }
    
    private var confirmationTimer: Timer?
    private let confirmationGracePeriod: TimeInterval = Constants.DisasterResponseGracePeriod
    
    private override init() {
        super.init()
        
        sosBeacon = SOSBeacon()
        sirenPlayer = SirenPlayer()
        
        disasterDetector = DisasterDetector(
            accelerationThreshold: Constants.SensorAccelerationThreshold,
            rotationThreshold: Constants.SensorRotationThreshold,
            pressureThreshold: Constants.SensorPressureThreshold,
            eventTimeWindow: Constants.DisasterTemporalCorrelationTimeWindow
        )
        
        sirenPlayer.delegate = self
        sosBeacon.delegate = self
        
        disasterDetector.delegate = self
        
        // setup local notifications
        setupNotificationCategories()
        UNUserNotificationCenter.current().delegate = self
        requestNotificationPermissions()
    }
    
    deinit {
        stopSOS()
        
        disasterDetector.stopDetection()
    }
    
    func startDetector() {
        guard
            isDetectorAvailable
                && !isDetectorActive
        else { return }
        
        disasterDetector.startDetection()
        
        delegate?.detectorStarted()
    }
    
    func stopDetector() {
        disasterDetector.stopDetection()
        
        delegate?.detectorStopped()
    }
    
    @objc func startSOS() {
        guard
            isSOSAvailable
                && !isSOSActive
        else { return }
        
        sosBeacon.startBroadcasting()
        sirenPlayer.startSiren()
        
        delegate?.sosStarted()
    }
    
    func stopSOS() {
        sirenPlayer.stopSiren()
        sosBeacon.stopBroadcasting()
        
        confirmationTimer?.invalidate()
        confirmationTimer = nil
        
        delegate?.sosStopped()
    }
    
    // starts confirmation timer for SOS
    func startConfirmationTimer() {
        confirmationTimer?.invalidate()
        
        // Schedule a new timer
        confirmationTimer = Timer.scheduledTimer(
            timeInterval: confirmationGracePeriod,
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
        delegate?.sosAvailabilityUpdate(isSOSAvailable)
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
        delegate?.sosAvailabilityUpdate(isSOSAvailable)
    }
}

extension EmergencyManager: DisasterDetectorDelegate {
    // called when an disaster is detected
    func disasterDetected() {
        // handle disaster detected
        delegate?.disasterDetected()
        
        // schedule a notification if app is in background
        if UIApplication.shared.applicationState != .active {
            scheduleDisasterNotification()
        }
        
        // start confirmation timer
        startConfirmationTimer()
    }
    
    // called when disaster detection starts
    func disasterDetectionStarted() {
        // do nothing
    }
    
    // called when disaster detection stops
    func disasterDetectionStopped() {
        // do nothing
    }
    
    // called when disaster detector availablility changes
    func disasterDetectorAvailabilityUpdate(
        _ isAvailable: Bool
    ) {
        NSLog("EmergencyManager: disaster detector\(isAvailable ? "" : " not") available")
        
        delegate?.detectorAvailabilityUpdate(isDetectorAvailable)
        
        if !isAvailable {
            stopDetector()
        } else {
            startDetector()
        }
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
            identifier: "DISASTER_CATEGORY",
            actions: [respondAction, helpAction],
            intentIdentifiers: [],
            options: []
        )
        
        UNUserNotificationCenter.current().setNotificationCategories([category])
    }
    
    // schedules a notification when a disaster is detected
    private func scheduleDisasterNotification() {
        let content = UNMutableNotificationContent()
        
        content.title = "Disaster Detected"
        content.body = "Are you okay?"
        content.sound = .default
        content.categoryIdentifier = "DISASTER_CATEGORY"
        
        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: 1,
            repeats: false
        )
        
        let request = UNNotificationRequest(
            identifier: Constants.DisasterResponseNotificationID,
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
    func disasterDetected()
    
    func detectorStarted()
    func detectorStopped()
    
    func detectorAvailabilityUpdate(_ isAvailable: Bool)
    
    func sosStarted()
    func sosStopped()
    
    func sosAvailabilityUpdate(_ isAvailable: Bool)
}
