package me.nizarmah.igatha.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.nizarmah.igatha.ui.theme.IgathaTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import me.nizarmah.igatha.ui.component.Section
import me.nizarmah.igatha.ui.component.SectionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(onBackClick: () -> Unit) {
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
                Section (
                    header = "Background services",
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
                            checked = true,
                            onCheckedChange = {
                                // TODO: Add view model logic
                            }
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
        SettingsView(onBackClick = {})
    }
}
