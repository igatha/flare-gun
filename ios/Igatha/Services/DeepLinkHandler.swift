//
//  DeepLinkHandler.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 22/04/2025.
//

import Foundation
import SwiftUI

class DeepLinkHandler: ObservableObject {
    static let shared = DeepLinkHandler()

    @Published var showSettings = false

    private init() {
        // Listen for deep link events

        registerSettingsObserver()
    }

    // registerSettingsObserver listens for settings deep link notification events
    private func registerSettingsObserver() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleSettings),
            name: NSNotification.Name(Constants.DeepLink.Settings.Name),
            object: nil
        )
    }

    // handleSettings opens the settings view
    @objc @MainActor private func handleSettings() {
        showSettings = true
    }

    // handleDeepLink is called from the outside to handle a deep link
    @MainActor func handleDeepLink(_ url: URL) -> Bool {
        // Check if the URL scheme is igatha
        guard let scheme = url.scheme, scheme == Constants.DeepLink.Scheme else {
            return false
        }

        // Check if the host is feedback
        if url.host == Constants.DeepLink.Settings.Value {
            handleSettings()
            return true
        }

        return false
    }
}
