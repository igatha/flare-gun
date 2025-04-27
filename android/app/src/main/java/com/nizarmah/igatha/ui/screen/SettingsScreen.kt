package com.nizarmah.igatha.ui.screen

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nizarmah.igatha.ui.view.SettingsView
import com.nizarmah.igatha.viewmodel.SettingsViewModel
import com.nizarmah.igatha.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onFeedbackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val disasterDetectionEnabled by viewModel.disasterDetectionEnabled.collectAsState()
    val isDisasterDetectionAvailable by viewModel.isDisasterDetectionAvailable.collectAsState()

    SettingsView(
        disasterDetectionEnabled = disasterDetectionEnabled,
        isDisasterDetectionAvailable = isDisasterDetectionAvailable,
        onDisasterDetectionEnabledChanged = { enabled ->
            viewModel.setDisasterDetectionEnabled(enabled)
        },
        onBackClick = onBackClick,
        onFeedbackClick = onFeedbackClick
    )
}
