//
//  AppDelegate.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 14/10/2024.
//

import SwiftUI
import UserNotifications

class AppDelegate: UIResponder, UIApplicationDelegate {
    var emergencyManager: EmergencyManager!
    var notificationManager: NotificationManager!
    var deepLinkHandler: DeepLinkHandler!

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions:
        [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Initialize managers in the background
        Task {
            await setupManagers()
        }

        return true
    }

    private func setupManagers() async {
        // Initialize notification manager first since it needs to be set as delegate
        notificationManager = NotificationManager.shared

        // Initialize emergency manager
        emergencyManager = EmergencyManager.shared

        // Initialize deep link handler
        deepLinkHandler = DeepLinkHandler.shared

        // Set up notification manager (will request permissions and schedule notifications)
        await notificationManager.setup()
    }

    // Handle deep links when app is launched via URL
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        return deepLinkHandler.handleDeepLink(url)
    }

    // Handle deep links in universal links format
    func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void
    ) -> Bool {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb, let url = userActivity.webpageURL {
            return deepLinkHandler.handleDeepLink(url)
        }

        return false
    }
}
