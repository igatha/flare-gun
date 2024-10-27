package me.nizarmah.igatha.ui.screen

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nizarmah.igatha.ui.view.SettingsView
import me.nizarmah.igatha.viewmodel.SettingsViewModel
import me.nizarmah.igatha.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val disasterDetectionEnabled by viewModel.disasterDetectionEnabled.collectAsState()

    SettingsView(
        disasterDetectionEnabled = disasterDetectionEnabled,
        onDisasterDetectionEnabledChanged = { isChecked ->
            viewModel.setDisasterDetectionEnabled(isChecked)
        },
        onBackClick = onBackClick
    )
}
