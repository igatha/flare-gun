//
//  FeedbackFormView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 20/04/2025.
//

import SwiftUI

struct FeedbackFormView: View {
    // Store feedback form inputs
    @State private var selectedReasons: Set<UsageReason> = []
    @State private var otherReasonDescription: String = ""
    @State private var helpfulnessDescription: String = ""
    @State private var contactEmail: String = ""

    // Retrieve all possible reasons
    let reasons = UsageReason.allCases

    var body: some View {
        Form {
            // usage reason options
            Section {
                // display each reason as a tappable row
                ForEach(reasons) { reason in
                    HStack {
                        // text
                        VStack(alignment: .leading) {
                            // text
                            Text(reason.displayString)

                            // subtext (examples)
                            if let example = reason.exampleString {
                                Text(example)
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                        }

                        // push the checkmark to the right
                        Spacer()

                        // show checkmark if selected
                        if selectedReasons.contains(reason) {
                            Image(systemName: "checkmark")
                                .foregroundColor(.accentColor)
                        }
                    }
                    // ensure the whole row area is tappable
                    .contentShape(Rectangle())
                    .onTapGesture {
                        // toggle reason selection in the Set
                        if selectedReasons.contains(reason) {
                            selectedReasons.remove(reason)

                            // clear description when 'other' is deselected
                            if reason == .other {
                                otherReasonDescription = ""
                            }
                        } else {
                            selectedReasons.insert(reason)
                        }
                    }
                }

                // only show a text field when 'other' is selected
                if selectedReasons.contains(.other) {
                    TextField("Please describe", text: $otherReasonDescription)
                }
            } header: {
                Text("Why do you use Igatha?")
            } footer: {
                Text("Select all that apply.")
            }

            // helpfulness feedback
            Section {
                TextEditor(text: $helpfulnessDescription)
            } header: {
                Text("What would make Igatha more helpful?")
            } footer: {
                Text("Optional. If there's anything on your mind, don't hesitate.")
            }

            // contact information
            Section {
                TextField("Your email address", text: $contactEmail)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
            } header: {
                Text("Would you like us to contact you?")
            } footer: {
                Text("Optional. We'll only use this if we have follow-up questions about your feedback.")
            }
        }
        .navigationTitle("Share Feedback")
    }
}


// Defines the reasons for using the app
enum UsageReason: Hashable, Identifiable, CaseIterable {
    case disasterPreparedness
    case adventureTravel
    case caregiving
    case workplaceSafety
    case regionalConflict
    case other

    var id: Self { self }

    // all cases for iteration
    static var allCases: [UsageReason] = [
        .disasterPreparedness, .adventureTravel, .caregiving, .workplaceSafety, .regionalConflict, .other
    ]

    // text
    var displayString: String {
        switch self {
        case .disasterPreparedness: return "Disaster Preparedness"
        case .adventureTravel: return "Adventure & Travel"
        case .caregiving: return "Caregiving"
        case .workplaceSafety: return "Workplace Safety"
        case .regionalConflict: return "Regional Conflict or Instability"
        case .other: return "Other"
        }
    }

    // subtext (examples)
    var exampleString: String? {
        switch self {
        case .disasterPreparedness: return "e.g., earthquakes, floods, conflicts"
        case .adventureTravel: return "e.g., hiking, biking, remote trips"
        case .caregiving: return "e.g., monitoring elderly or dependents"
        case .workplaceSafety: return "e.g., construction, mining, field jobs"
        case .regionalConflict: return "e.g., war, civil unrest, political instability"
        case .other: return "Please specify below"
        }
    }
}

// preview provider
struct FeedbackFormView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            FeedbackFormView()
        }
    }
}
