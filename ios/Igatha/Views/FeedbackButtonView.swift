//
//  FeedbackRowView.swift
//  Igatha
//
//  Created by Nizar Mahmoud on 20/04/2025.
//

import SwiftUI

struct FeedbackButtonView: View {
    var body: some View {
        NavigationLink(
            destination: FeedbackFormView()
        ) {
            FeedbackButtonContentView()
        }
        .padding(.vertical)
        .padding(.horizontal, 20)
        .contentShape(Rectangle())
        .background(
            LinearGradient(
                // pink-ish gradient
                gradient: Gradient(colors: [
                    Color.pink.opacity(0.6),
                    Color.purple.opacity(0.6)
                ]),
                // 135 degrees gradient
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .shadow(color: .gray.opacity(0.4), radius: 5, x: 0, y: 2)
        // Set chevron color to white.
        .foregroundStyle(.white, .white)
    }
}

struct FeedbackButtonContentView: View {
    var body: some View {
        HStack(spacing: 16) {
            // heart icon
            Image(systemName: "heart")
                .resizable()
                .scaledToFit()
                .frame(width: 30, height: 30)
                .foregroundColor(.white)
            
            VStack(alignment: .leading, spacing: 4) {
                // text
                Text("Tell us why you use Igatha")
                    .font(.headline)
                    .foregroundColor(.white)
                
                // subtext
                Text("It helps us make it more reliable.")
                    .font(.subheadline)
                    .foregroundColor(.white.opacity(0.9))
            }
        }
    }
}

struct FeedbackButtonView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            List {
                FeedbackButtonView()
                // removes the section padding around the feedback row
                    .listRowInsets(EdgeInsets())
            }
        }
    }
}
