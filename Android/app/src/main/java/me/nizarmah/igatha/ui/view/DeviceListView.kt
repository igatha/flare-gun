package me.nizarmah.igatha.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.nizarmah.igatha.model.Device
import me.nizarmah.igatha.ui.theme.IgathaTheme
import java.util.Date
import java.util.UUID

@Composable
fun DeviceListView(devices: List<Device>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        item {
            Text(
                "PEOPLE SEEKING HELP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 20.dp)
            )
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (devices.isEmpty()) {
                        Text(
                            "No devices found nearby.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    } else {
                        devices.forEachIndexed { index, device ->
                            DeviceRowView(
                                device = device,
                                onClick = {
                                    // TODO: Implement navigation to DeviceDetailView
                                }
                            )

                            if (index < devices.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                "Note: Distance is approximate and varies due to signal fluctuations. It is for general guidance only.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 20.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DeviceListViewPreview() {
    val mockDevices = listOf(
        Device(id = UUID.randomUUID(), rssi = -40.0),
        Device(id = UUID.randomUUID(), rssi = -60.0, lastSeen = Date(System.currentTimeMillis() - 600_000)),
        Device(id = UUID.randomUUID(), rssi = -75.0),
        Device(id = UUID.randomUUID(), rssi = -85.0)
    )

    IgathaTheme {
        DeviceListView(devices = mockDevices)
    }
}
