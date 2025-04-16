//
//  SettingsViewModel.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 17/10/2024.
//

import SwiftUI

class SettingsViewModel: ObservableObject {
    @AppStorage(Constants.DisasterDetectionSettingsKey)
    var disasterDetectionEnabled: Bool = true {
        didSet {
            if disasterDetectionEnabled {
                DispatchQueue.global(qos: .background).async {
                    EmergencyManager.shared.startDetector()
                }
            } else {
                DispatchQueue.global(qos: .background).async {
                    EmergencyManager.shared.stopDetector()
                }
            }
        }
    }
}
