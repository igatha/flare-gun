package com.nizarmah.igatha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID

/**
 * FeedbackFormViewModel handles all logic for the feedback form view.
 */
class FeedbackFormViewModel(
    app: Application,
    private val feedbackService: FeedbackService = UsageFeedbackGoogleForm(Dispatchers.IO)
) : AndroidViewModel(app) {

    // Form state
    private val _formState = MutableStateFlow<FormState>(FormState.Idle)
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    // Submission result
    private val _submissionResult = MutableStateFlow<SubmissionResult?>(null)
    val submissionResult: StateFlow<SubmissionResult?> = _submissionResult.asStateFlow()

    // Form data
    private val _usageReasons = MutableStateFlow<Set<UsageReason>>(emptySet())

    // Form bindings
    private val _customUsage = MutableStateFlow("")
    val customUsage: StateFlow<String> = _customUsage.asStateFlow()

    private val _ideas = MutableStateFlow("")
    val ideas: StateFlow<String> = _ideas.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // Checks if the user selected the "other" usage reason
    val hasCustomUsage: StateFlow<Boolean> = MutableStateFlow(false).apply {
        viewModelScope.launch {
            _usageReasons.collect { reasons ->
                value = reasons.contains(UsageReason.Other)
            }
        }
    }

    // Updates the form data
    fun updateCustomUsage(value: String) {
        _customUsage.value = value
    }

    fun updateIdeas(value: String) {
        _ideas.value = value
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    // Checks if the usage reason is selected
    fun isUsageReasonSelected(reason: UsageReason): Boolean {
        return _usageReasons.value.contains(reason)
    }

    // Toggles the usage reason
    fun toggleUsageReason(reason: UsageReason) {
        val currentReasons = _usageReasons.value.toMutableSet()
        if (currentReasons.contains(reason)) {
            currentReasons.remove(reason)
        } else {
            currentReasons.add(reason)
        }
        _usageReasons.value = currentReasons
    }

    // Dismisses submission result alert
    fun dismissAlert() {
        _submissionResult.value = null
    }

    // Validates the form and returns the error if it's invalid
    fun validateForm(): String? {
        // Form is valid if at least one usage reason is selected
        if (_usageReasons.value.isEmpty()) {
            return "Please select at least one usage reason."
        }

        // If "other" is selected, custom usage should not be empty
        if (_usageReasons.value.contains(UsageReason.Other) &&
            _customUsage.value.trim().isEmpty()) {
            return "Please specify why you chose 'other'."
        }

        return null
    }

    // Submits the feedback form
    fun submit() {
        // Validate the form and return the error if it's invalid
        val errorMessage = validateForm()
        if (errorMessage != null) {
            _submissionResult.value = SubmissionResult.Error(errorMessage)
            return
        }

        // Update form state to submitting
        _formState.value = FormState.Submitting

        // Create a feedback object
        val feedback = Feedback(
            usageReasons = _usageReasons.value,
            customUsage = _customUsage.value,
            ideas = _ideas.value,
            email = _email.value
        )

        // Submit the form asynchronously
        viewModelScope.launch {
            try {
                // Try to submit the form
                feedbackService.submit(feedback)

                // Show the success alert
                _submissionResult.value = SubmissionResult.Success
            } catch (e: Exception) {
                // Show the error alert with a more descriptive message
                val errorMessage = when {
                    e.message.isNullOrBlank() -> "Connection error. Check your internet connection."
                    else -> e.message
                }

                // Generate a truncated reference ID
                val refId = UUID.randomUUID().toString().substring(0, 8)

                // Log the error with full details for troubleshooting (shows in logcat)
                android.util.Log.e(
                    "FeedbackForm",
                    "Error submitting form - Ref: $refId - Details: $errorMessage",
                    e
                )

                _submissionResult.value = SubmissionResult.Error(
                    "Your feedback could not be submitted. Please try again later. (Ref: $refId)"
                )
            } finally {
                // Update form state to idle
                _formState.value = FormState.Idle
            }
        }
    }
}

/**
 * Interface for feedback services to abstract the submission process.
 */
interface FeedbackService {
    suspend fun submit(feedback: Feedback)
}

/**
 * UsageFeedbackGoogleForm has the submission logic for https://forms.gle/rcu3MZjPYww7Fbnh7.
 * This class performs network operations in a coroutine context.
 */
class UsageFeedbackGoogleForm(private val ioDispatcher: CoroutineDispatcher) : FeedbackService {
    // Form URL for the POST request
    private val formUrl = "https://docs.google.com/forms/u/0/d/e/1FAIpQLSdCdNYIaPcg2-eAs1Mlvwoa6P5Ijqfdb1hmWlaA-poIKpMDtQ/formResponse"

    // Feedback field identifier
    private val feedbackField = "entry.457989095"

    // Submits the feedback form
    override suspend fun submit(feedback: Feedback) = withContext(ioDispatcher) {
        // Create a URL connection
        val url = URL(formUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            // Set up the connection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.connectTimeout = 15000 // 15 seconds timeout
            connection.readTimeout = 15000 // 15 seconds timeout

            // Create the form data
            val postData = "${feedbackField}=${URLEncoder.encode(feedback.toJson(), "UTF-8")}"

            // Send the request
            connection.outputStream.use { os ->
                val input = postData.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            // Check the response code
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_MOVED_TEMP) {
                throw Exception("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.message ?: e.javaClass.simpleName}")
        } finally {
            connection.disconnect()
        }
    }
}

/**
 * Feedback stores the form data and converts it to a JSON string.
 */
data class Feedback(
    val usageReasons: Set<UsageReason>,
    val customUsage: String,
    val ideas: String,
    val email: String
) {
    // Constant form data
    private val device = "android"
    private val key = "android-usage-feedback-v1.0.0"

    // Converts the form data to a JSON string
    fun toJson(): String {
        val jsonObject = JSONObject().apply {
            put("usage", JSONObject().apply {
                put("reasons", usageReasons.map { it.displayString })
                put("custom", customUsage)
            })
            put("ideas", ideas)
            put("email", email)
            put("device", device)
            put("key", key)
        }

        return jsonObject.toString()
    }
}

/**
 * FormState stores the state of the form.
 */
sealed class FormState {
    object Idle : FormState()
    object Submitting : FormState()
}

/**
 * SubmissionResult stores the result of the form submission.
 */
sealed class SubmissionResult {
    object Success : SubmissionResult()
    data class Error(val message: String) : SubmissionResult()
}

/**
 * UsageReason stores all usage reasons and their examples.
 */
enum class UsageReason {
    DisasterPreparedness,
    AdventureTravel,
    Caregiving,
    WorkplaceSafety,
    RegionalConflict,
    Other;

    // Display string is the primary text for the usage reason
    val displayString: String
        get() = when (this) {
            DisasterPreparedness -> "Disaster Preparedness"
            AdventureTravel -> "Adventure & Travel"
            Caregiving -> "Caregiving"
            WorkplaceSafety -> "Workplace Safety"
            RegionalConflict -> "Regional Conflict or Instability"
            Other -> "Other"
        }

    // Example string is the subtext for the usage reason
    val exampleString: String?
        get() = when (this) {
            DisasterPreparedness -> "e.g., earthquakes, floods, conflicts"
            AdventureTravel -> "e.g., hiking, biking, remote trips"
            Caregiving -> "e.g., monitoring elderly or dependents"
            WorkplaceSafety -> "e.g., construction, mining, field jobs"
            RegionalConflict -> "e.g., war, civil unrest, political instability"
            Other -> "Please specify below"
        }

    companion object {
        // All cases for iteration
        val allCases: List<UsageReason> = entries.toList()
    }
}

/* Notes

 [1]:
    I am aware that the Google form can be spammed.
    I care most about emails, and this helps me reach users directly.
    I made sure there's no long term or financial damage from spam.
    This cheap implementation to get emails here outweighs any other.

 */
