package me.nizarmah.igatha.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.nizarmah.igatha.ui.component.Section
import me.nizarmah.igatha.ui.component.SectionItem
import me.nizarmah.igatha.ui.theme.IgathaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    disasterDetectionEnabled: Boolean,
    onDisasterDetectionEnabledChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        TopAppBar(
            title = { Text("Settings") },
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
        LazyColumn {
            item {
                Section(
                    header = "Background Services",
                    footer = "Services might require additional permissions."
                ) {
                    SectionItem {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Disaster Detection",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "Detects disasters and sends SOS when the app is not in use. This requires location permission. This may increase battery consumption.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.padding(2.dp))

                        Switch(
                            checked = disasterDetectionEnabled,
                            onCheckedChange = onDisasterDetectionEnabledChanged
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsViewPreview() {
    IgathaTheme {
        SettingsView(
            disasterDetectionEnabled = true,
            onDisasterDetectionEnabledChanged = {},
            onBackClick = {}
        )
    }
}
