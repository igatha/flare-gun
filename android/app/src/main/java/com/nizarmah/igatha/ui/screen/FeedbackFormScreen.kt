package com.nizarmah.igatha.ui.screen

import android.app.Application
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nizarmah.igatha.ui.view.FeedbackFormView
import com.nizarmah.igatha.viewmodel.FeedbackFormViewModel
import com.nizarmah.igatha.viewmodel.FeedbackFormViewModelFactory
import com.nizarmah.igatha.viewmodel.FormState
import com.nizarmah.igatha.viewmodel.SubmissionResult
import kotlinx.coroutines.launch

/**
 * Feedback form screen to understand why people use Igatha.
 * This screen is responsible for coordination between the view and viewmodel.
 */
@Composable
fun FeedbackFormScreen(
    onNavigateBack: () -> Unit
) {
    // Create ViewModel with Factory
    val application = LocalContext.current.applicationContext as Application
    val viewModel: FeedbackFormViewModel = viewModel(
        factory = FeedbackFormViewModelFactory(application)
    )

    // Collect state from ViewModel
    val formState by viewModel.formState.collectAsState()
    val submissionResult by viewModel.submissionResult.collectAsState()
    val hasCustomUsage by viewModel.hasCustomUsage.collectAsState()
    val customUsage by viewModel.customUsage.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val email by viewModel.email.collectAsState()

    // For actions that need coroutine scope
    val scope = rememberCoroutineScope()

    // Use the FeedbackFormView from ui.view
    FeedbackFormView(
        formState = formState,
        customUsage = customUsage,
        ideas = ideas,
        email = email,
        hasCustomUsage = hasCustomUsage,
        isUsageReasonSelected = { viewModel.isUsageReasonSelected(it) },
        onUsageReasonToggle = { viewModel.toggleUsageReason(it) },
        onCustomUsageChange = { viewModel.updateCustomUsage(it) },
        onIdeasChange = { viewModel.updateIdeas(it) },
        onEmailChange = { viewModel.updateEmail(it) },
        onSubmit = {
            if (formState == FormState.Idle) {
                scope.launch {
                    viewModel.submit()
                }
            }
        },
        onBackClick = onNavigateBack
    )

    // Alert dialogs for submission results
    submissionResult?.let { result ->
        when (result) {
            is SubmissionResult.Success -> {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.dismissAlert()
                        onNavigateBack()
                    },
                    title = { Text("Thank you!") },
                    text = { Text("Your feedback helps us improve Igatha.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.dismissAlert()
                                onNavigateBack()
                            }
                        ) {
                            Text("Done", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
            is SubmissionResult.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissAlert() },
                    title = { Text("Error") },
                    text = { Text(result.message) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.dismissAlert() }) {
                            Text("OK", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        }
    }
}