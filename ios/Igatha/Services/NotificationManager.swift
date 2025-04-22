//
//  NotificationManager.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 22/04/2025.
//

import Foundation
import UserNotifications
import SwiftUI

class NotificationManager: NSObject, UNUserNotificationCenterDelegate {
    static let shared = NotificationManager()

    // setup is called when the app is launched
    func setup() async {
        do {
            try await requestAuthorization()
            await scheduleFeedbackRequestNotification()
        } catch {
            NSLog("Error setting up notification manager: \(error)")
        }
    }

    private override init() {
        super.init()

        // Set the delegate for the notification center
        UNUserNotificationCenter.current().delegate = self
    }

    // requestAuthorization requests notification permission
    private func requestAuthorization() async throws {
        let options: UNAuthorizationOptions = [.alert, .sound, .badge]
        try await UNUserNotificationCenter.current().requestAuthorization(options: options)
    }

    // scheduleFeedbackRequestNotification schedules the feedback request notification
    private func scheduleFeedbackRequestNotification() async {
        // Ignore if we have already scheduled the feedback request notification
        if UserDefaults.standard.object(
            forKey: Constants.Notifications.Feedback.TimestampKey
        ) != nil {
            return
        }

        let timestamp = Date()

        NSLog("Scheduling feedback request notification at \(timestamp)")

        // Store the timestamp when the notification was scheduled
        UserDefaults.standard.set(
            timestamp,
            forKey: Constants.Notifications.Feedback.TimestampKey
        )

        // Create the notification content
        let content = UNMutableNotificationContent()
        content.title = "Tell us why you use Igatha"
        content.body = "Help us improve it for you and others"
        content.sound = .default
        content.userInfo = [
            Constants.DeepLink.Key: Constants.Notifications.Feedback.Link.Value
        ]

        // Schedule the notification for the future
        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: Constants.Notifications.Feedback.TriggerDelay,
            repeats: false
        )

        // Create the notification request
        let request = UNNotificationRequest(
            identifier: Constants.Notifications.Feedback.Id,
            content: content,
            trigger: trigger
        )

        // Add the request to the notification center with async/await
        do {
            try await UNUserNotificationCenter.current().add(request)
        } catch {
            NSLog("Error scheduling feedback request notification: \(error)")
        }
    }

    // userNotificationCenter handles the notification response
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        // Get the identifier of the notification
        let id = response.notification.request.identifier

        // Handle the feedback request notification
        if id == Constants.Notifications.Feedback.Id {
            if
                let deepLink = response.notification.request.content.userInfo[Constants.DeepLink.Key] as? String,
                deepLink == Constants.Notifications.Feedback.Link.Value
            {
                // Post notification on the main thread
                Task {
                    await MainActor.run {
                        NotificationCenter.default.post(
                            name: NSNotification.Name(Constants.Notifications.Feedback.Link.Name),
                            object: nil
                        )
                    }
                }
            }
        }

        completionHandler()
    }

    // userNotificationCenter handles the notification presentation
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        // Show the notification even if the app is in foreground
        completionHandler([.banner, .sound])
    }
}
