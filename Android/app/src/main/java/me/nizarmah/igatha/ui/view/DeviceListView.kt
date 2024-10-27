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
import me.nizarmah.igatha.ui.component.Section
import me.nizarmah.igatha.ui.theme.IgathaTheme
import java.util.Date
import java.util.UUID

@Composable
fun DeviceListView(devices: List<Device>, onDeviceClick: (Device) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        item {
            Section (
                header = "People seeking help",
                footer = "Note: Distance is approximate and varies due to signal fluctuations. It is for general guidance only."
            ) {
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
                            onClick = { onDeviceClick(device) }
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
        DeviceListView(devices = mockDevices) {
            // TODO: Implement navigation to DeviceDetailView
        }
    }
}
