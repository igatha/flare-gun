//
//  AppDelegate.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import SwiftUI

class AppDelegate: UIResponder, UIApplicationDelegate {    
    var emergencyManager: EmergencyManager!
    
    func application(
        _ application: UIApplication,
        willFinishLaunchingWithOptions launchOptions:
        [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        return true
    }
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions:
        [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // initialize EmergencyManager
        emergencyManager = EmergencyManager.shared
        
        return true
    }
    
    func application(
        _ application: UIApplication,
        shouldSaveSecureApplicationState coder: NSCoder
    ) -> Bool {
        return true
    }
    
    func application(
        _ application: UIApplication,
        shouldRestoreSecureApplicationState coder: NSCoder
    ) -> Bool {
        return true
    }
}
