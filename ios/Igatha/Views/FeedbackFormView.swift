//
//  FeedbackFormView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 20/04/2025.
//

import SwiftUI

// Feedback form to understand why people use Igatha.
struct FeedbackFormView: View {
    // vm is the view model with the form logic.
    @StateObject private var vm = FeedbackFormViewModel()
    
    // dismiss is the environment variable that dismisses the current view.
    // This is not the best approach, but it's good enough for this case.
    @Environment(\.dismiss) private var dismiss
    
    // body is the main view of the form.
    var body: some View {
        Form {
            // Usage reasons.
            Section {
                ForEach(UsageReason.allCases) { reason in
                    HStack {
                        // Reason.
                        VStack(alignment: .leading) {
                            // Text.
                            Text(reason.displayString)
                            
                            // Subtext.
                            if let examples = reason.exampleString {
                                Text(examples).font(.caption).foregroundColor(.secondary)
                            }
                        }
                        
                        Spacer()
                        
                        // If selected, show checkmark.
                        if vm.isUsageReasonSelected(reason) {
                            Image(systemName: "checkmark").foregroundColor(.accentColor)
                        }
                    }
                    // Make the row is tappable.
                    .contentShape(Rectangle())
                    .onTapGesture {
                        vm.toggleUsageReason(reason)
                    }
                }
                
                // If other reason, show text field.
                if vm.hasCustomUsage {
                    TextField("Please describe", text: $vm.customUsage)
                }
            } header: {
                Text("Why do you use Igatha?")
            } footer: {
                Text("Select all that apply.")
            }
            
            // Ideas.
            Section {
                TextEditor(text: $vm.ideas).frame(minHeight: 90)
            } header: {
                Text("What would make Igatha more helpful?")
            } footer: {
                Text("Optional. If you have any ideas, don't hesitate.")
            }
            
            // Email.
            Section {
                TextField("Email address", text: $vm.email)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
            } header: {
                Text("Your email")
            } footer: {
                Text("Optional. We'll only contact you for clarifications.")
            }
            
            // Submit.
            Section {
                Button(action: {
                    guard vm.formState == .idle else { return }
                    
                    // Run the async submit function in a Task
                    Task { await vm.submit() }
                }) {
                    HStack {
                        Text("Submit")
                        
                        Spacer()
                        
                        // If submitting, show progress view.
                        if vm.formState == .submitting {
                            ProgressView()
                        }
                    }
                }
                .disabled(vm.formState == .submitting)
            }
        }
        .navigationBarTitle("Share Feedback", displayMode: .inline)
        .alert(item: $vm.submissionResult) { result in
            switch result {
            case .success:
                return Alert(
                    title: Text("Thank you!"),
                    message: Text("Your feedback helps us improve Igatha."),
                    dismissButton: .cancel(Text("Done")) {
                        vm.dismissAlert()
                        dismiss()
                    }
                )
            case .err(let message):
                return Alert(
                    title: Text("Error"),
                    message: Text(message),
                    dismissButton: .cancel(Text("OK")) {
                        vm.dismissAlert()
                    }
                )
            }
        }
    }
}

struct FeedbackFormView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView { FeedbackFormView() }
    }
}
