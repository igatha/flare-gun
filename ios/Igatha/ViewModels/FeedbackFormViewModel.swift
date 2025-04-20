//
//  FeedbackFormViewModel.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 20/04/2025.
//

import SwiftUI

// FeedbackFormViewModel handles all logic for the feedback form view.
@MainActor
class FeedbackFormViewModel: ObservableObject {
    
    // formState stores the state of the form.
    @Published public var formState: FormState = .idle
    
    // submissionResult stores the result of the form submission.
    @Published public var submissionResult: SubmissionResult?
    
    // usageReasons stores all the usage reasons the user selected.
    @Published public var usageReasons: Set<UsageReason> = []
    
    // Form bindings.
    @Published public var customUsage: String = ""
    @Published public var ideas: String = ""
    @Published public var email: String = ""
    
    // hasCustomUsage checks if the user selected the "other" usage reason.
    public var hasCustomUsage: Bool {
        return usageReasons.contains(.other)
    }
    
    // isUsageReasonSelected checks if the usage reason is selected.
    public func isUsageReasonSelected(_ reason: UsageReason) -> Bool {
        return usageReasons.contains(reason)
    }
    
    // toggleUsageReason updates the selected usage reasons.
    public func toggleUsageReason(_ reason: UsageReason) {
        // If reason is already selected, remove it.
        guard !usageReasons.contains(reason) else {
            usageReasons.remove(reason)
            return
        }
        
        // Otherwise, add it.
        usageReasons.insert(reason)
    }
    
    // dismissAlert dismisses submission result alert.
    public func dismissAlert() {
        submissionResult = nil
    }
    
    // validateForm validates the form and returns the error if it's invalid.
    public func validateForm() -> String? {
        // Form is valid if at least one usage reason is selected.
        guard !usageReasons.isEmpty else {
            return "Please select at least one usage reason."
        }
        
        // If "other" is selected, custom usage should not be empty.
        if usageReasons.contains(.other) && customUsage.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return "Please specify why you chose 'other'."
        }
        
        return nil
    }
    
    // submit submits the feedback form.
    public func submit() async {
        // Validate the form and return the error if it's invalid.
        if let errMsg = validateForm() {
            submissionResult = .err(errMsg)
            return
        }
        
        // Update form state to submitting.
        formState = .submitting
        
        let feedback = Feedback(
            usageReasons: usageReasons,
            customUsage: customUsage,
            ideas: ideas,
            email: email
        )
        
        let form = UsageFeedbackGoogleForm(feedback: feedback)
        
        // Submit the form asynchronously.
        do {
            // Try to submit the form.
            try await form.submit()
            
            // Show the success alert. Updates are safe due to @MainActor.
            submissionResult = .success
        } catch {
            // Show the error alert. Updates are safe due to @MainActor.
            submissionResult = .err("Your feedback could not be submitted. Please try again later.")
        }
        
        // Update form state to idle. Updates are safe due to @MainActor.
        formState = .idle
    }
}

// UsageFeedbackGoogleForm has the submission logic for https://forms.gle/rcu3MZjPYww7Fbnh7. [1]
// This class performs network operations and does not need to be @MainActor.
class UsageFeedbackGoogleForm {
    // formUrl is the URL for the POST request.
    private let formUrl = URL(string: "https://docs.google.com/forms/u/0/d/e/1FAIpQLSdCdNYIaPcg2-eAs1Mlvwoa6P5Ijqfdb1hmWlaA-poIKpMDtQ/formResponse")!
    
    // feedback is the feedback field value.
    private var feedback: Feedback
    // feedbackField is the feedback field identifier.
    private let feedbackField = "entry.457989095"
    
    // init initializes the form fields.
    init(feedback: Feedback) {
        self.feedback = feedback
    }
    
    // submit submits the feedback form.
    public func submit() async throws {
        // Create the request.
        var request = URLRequest(url: formUrl)
        request.httpMethod = "POST"
        
        // Google requires form to be submitted as a urlencoded-form.
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        
        // We pass the feedback to a single field for flexibility.
        var comps  = URLComponents()
        comps.queryItems = [ URLQueryItem(name: feedbackField, value: feedback.toJSON()) ]
        
        // Add the request body.
        request.httpBody = comps.percentEncodedQuery?.data(using: .utf8)
        
        // Send the request.
        let (data, response) = try await URLSession.shared.data(for: request)
        
        // Log the response only in non-release builds.
#if !RELEASE
        print("Response: \(response)")
        print("Data: \(data)")
#endif
        
        guard (response as? HTTPURLResponse)?.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
    }
}

// Feedback stores the form data and converts it to a JSON string.
struct Feedback {
    // Constant form data.
    private let device = "ios"
    private let key = "ios-usage-feedback-v1.0.0"
    
    // Dynamic form data.
    private var usageReasons: Set<UsageReason>
    private var customUsage: String
    private var ideas: String
    private var email: String
    
    // init initializes the form data from the view fields.
    init(
        usageReasons: Set<UsageReason>,
        customUsage: String,
        ideas: String,
        email: String
    ) {
        self.usageReasons = usageReasons
        self.customUsage = customUsage
        self.ideas = ideas
        self.email = email
    }
    
    // toJson converts the form data to a JSON string.
    public func toJSON() -> String {
        let dict: [String: Any] = [
            "usage": [
                "reasons": usageReasons.map { $0.displayString },
                "custom": customUsage,
            ],
            "ideas": ideas,
            "email": email,
            "device": device,
            "key": key,
        ]
        
        guard
            let data = try? JSONSerialization.data(withJSONObject: dict),
            let str  = String(data: data, encoding: .utf8)
        else {
            // Fallback to empty JSON string, instead of crashing.
            return "{}"
        }
        
        return str
    }
}

// FormState stores the state of the form.
enum FormState {
    case idle
    case submitting
}

// SubmissionResult stores the result of the form submission.
enum SubmissionResult: Identifiable {
    // id makes each submission result unique for SwiftUI
    var id: UUID { UUID() }
    
    case success
    case err(String)
}

// UsageReason stores all usage reasons and their examples.
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
    
    // displayString is the primary text for the usage reason.
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
    
    // exampleString is the subtext for the usage reason.
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

/* Notes

 [1]:
    I am aware that the Google form can be spammed.
    I care most about emails, and this helps me reach users directly.
    I made sure there's no long term or financial damage from spam.
    This cheap implementation to get emails here outweighs any other.

 */
