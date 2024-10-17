//
//  SettingsViewModel.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 17/10/2024.
//

import SwiftUI

class SettingsViewModel: ObservableObject {
    @AppStorage("disasterMonitoringEnabled")
    var disasterMonitoringEnabled: Bool = false {
        didSet {
            if disasterMonitoringEnabled {
                // TODO: Handle case
            } else {
                // TODO: Handle case
            }
        }
    }
}
