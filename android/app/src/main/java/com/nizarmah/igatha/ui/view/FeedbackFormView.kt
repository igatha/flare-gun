package com.nizarmah.igatha.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nizarmah.igatha.ui.component.Section
import com.nizarmah.igatha.ui.theme.IgathaTheme
import com.nizarmah.igatha.viewmodel.FormState
import com.nizarmah.igatha.viewmodel.UsageReason

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FeedbackFormView(
    formState: FormState,
    customUsage: String,
    ideas: String,
    email: String,
    hasCustomUsage: Boolean,
    isUsageReasonSelected: (UsageReason) -> Boolean,
    onUsageReasonToggle: (UsageReason) -> Unit,
    onCustomUsageChange: (String) -> Unit,
    onIdeasChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit
) {
    // Focus and keyboard management
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Feedback") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            LazyColumn {
                // Usage reasons section
                item {
                    Section(
                        header = "Why do you use Igatha?",
                        footer = "Select all that apply."
                    ) {
                        Column {
                            // Usage reason items
                            UsageReason.allCases.forEachIndexed { index, reason ->
                                UsageReasonItem(
                                    reason = reason,
                                    isSelected = isUsageReasonSelected(reason),
                                    onToggle = { onUsageReasonToggle(reason) }
                                )

                                // Add divider between items (except for the last one)
                                if (index < UsageReason.allCases.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                }
                            }

                            // Custom reason text field (conditional)
                            if (hasCustomUsage) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                TextField(
                                    value = customUsage,
                                    onValueChange = onCustomUsageChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Please describe") },
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    )
                                )
                            }
                        }
                    }
                }

                // Ideas section
                item {
                    Section(
                        header = "What would make Igatha more helpful?",
                        footer = "Optional. If you have any ideas, don't hesitate."
                    ) {
                        TextField(
                            value = ideas,
                            onValueChange = onIdeasChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )
                    }
                }

                // Email section
                item {
                    Section(
                        header = "Your email",
                        footer = "Optional. We'll only contact you for clarifications."
                    ) {
                        TextField(
                            value = email,
                            onValueChange = onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Email address") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                capitalization = KeyboardCapitalization.None,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            singleLine = true
                        )
                    }
                }

                // Submit button section
                item {
                    Section(
                        padding = Modifier.padding(vertical = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onSubmit()
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = formState == FormState.Idle,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Submit",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                // Show progress indicator if submitting
                                if (formState == FormState.Submitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // Spacer
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun UsageReasonItem(
    reason: UsageReason,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    // State that tracks the visual selection state, initially set to the actual selection state
    val (visuallySelected, setVisuallySelected) = androidx.compose.runtime.remember(isSelected) {
        androidx.compose.runtime.mutableStateOf(isSelected)
    }

    // Update the visual state when the actual selection state changes
    androidx.compose.runtime.LaunchedEffect(isSelected) {
        setVisuallySelected(isSelected)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Immediately update visual state for responsive feedback
                setVisuallySelected(!visuallySelected)
                // Then trigger the actual model update
                onToggle()
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reason content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reason.displayString,
                style = MaterialTheme.typography.bodyLarge
            )

            // Add spacing between title and example
            Spacer(modifier = Modifier.height(4.dp))

            // Subtext
            reason.exampleString?.let { example ->
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodySmall.fontSize.times(1.4f)
                )
            }
        }

        // Checkmark for selected reasons - use visuallySelected for immediate feedback
        if (visuallySelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackFormViewPreview() {
    IgathaTheme {
        FeedbackFormView(
            formState = FormState.Idle,
            customUsage = "I use it for my personal safety",
            ideas = "Would be nice to have offline maps",
            email = "user@example.com",
            hasCustomUsage = true,
            isUsageReasonSelected = { it in setOf(UsageReason.DisasterPreparedness, UsageReason.Other) },
            onUsageReasonToggle = {},
            onCustomUsageChange = {},
            onIdeasChange = {},
            onEmailChange = {},
            onSubmit = {},
            onBackClick = {}
        )
    }
}
