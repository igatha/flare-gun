package me.nizarmah.igatha.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.nizarmah.igatha.model.Device
import me.nizarmah.igatha.ui.theme.IgathaTheme
import me.nizarmah.igatha.ui.component.Section
import me.nizarmah.igatha.ui.component.SectionItem
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailView(
    device: Device,
    onBackClick: () -> Unit
) {
    val timeSinceLastSeen = remember(device.lastSeen) {
        calculateTimeSinceLastSeen(device.lastSeen)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        TopAppBar(
            title = { Text("Device Details") },
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
                    header = "Identity",
                    footer = "Identity is pseudonymized for privacy."
                ) {
                    SectionItem {
                        Text(text = "Name", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            text = device.shortName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    SectionItem {
                        Text(text = "ID", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            text = device.id,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            letterSpacing = (-0.5).sp
                        )
                    }
                }
            }
            item {
                Section(
                    header = "Location",
                    footer = "Location is limited by used tech. Direction is not available. Distance is approximate and varies due to signal fluctuations. It is for general guidance only."
                ) {
                    SectionItem {
                        Text(text = "Distance", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            text = String.format(
                                Locale.getDefault(),
                                "%.1f meters away",
                                device.estimateDistance()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            item {
                Section(
                    header = "Status",
                    footer = "Status shows if the device is active and in range."
                ) {
                    SectionItem {
                        Text(text = "Last Seen", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            text = timeSinceLastSeen,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun calculateTimeSinceLastSeen(lastSeen: Date): String {
    val now = Date()
    val diff = now.time - lastSeen.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7

    return when {
        seconds < 60 -> "$seconds second${if (seconds != 1L) "s" else ""} ago"
        minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
        hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
        days < 7 -> "$days day${if (days != 1L) "s" else ""} ago"
        else -> "$weeks week${if (weeks != 1L) "s" else ""} ago"
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceDetailViewPreview() {
    IgathaTheme {
        DeviceDetailView(
            device = Device(
                id = UUID.randomUUID(),
                rssi = -40.0
            ),
            onBackClick = {
                // do nothing
            }
        )
    }
}
