package com.nizarmah.igatha.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nizarmah.igatha.ui.theme.Gray
import com.nizarmah.igatha.ui.theme.IgathaTheme
import com.nizarmah.igatha.ui.theme.Red
import com.nizarmah.igatha.model.Device
import com.nizarmah.igatha.viewmodel.AlertType
import com.nizarmah.igatha.viewmodel.DisasterResponse
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView(
    isSOSAvailable: Boolean,
    isSOSActive: Boolean,
    devices: List<Device>,
    activeAlert: AlertType?,
    onSOSClick: () -> Unit,
    onConfirmSOS: () -> Unit,
    onDismissAlert: () -> Unit,
    onDisasterResponse: (DisasterResponse) -> Unit,
    onSettingsClick: () -> Unit,
    onDeviceClick: (Device) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DeviceListView(
                    devices = devices,
                    onDeviceClick = onDeviceClick
                )
            }

            SOSButton(
                isSOSAvailable = isSOSAvailable,
                isSOSActive = isSOSActive,
                onSOSClick = onSOSClick
            )
        }
    }

    // Handle alerts
    activeAlert?.let { alert ->
        when (alert) {
            is AlertType.SOSConfirmation -> {
                AlertDialog(
                    onDismissRequest = onDismissAlert,
                    title = { Text("Are you sure?") },
                    text = { Text("This will broadcast your location and start a loud siren.") },
                    confirmButton = {
                        TextButton(
                            onClick = onConfirmSOS
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = onDismissAlert
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            is AlertType.DisasterDetected -> {
                AlertDialog(
                    onDismissRequest = {
                        onDisasterResponse(DisasterResponse.ImOkay)
                    },
                    title = { Text("Disaster Detected") },
                    text = { Text("Are you okay?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDisasterResponse(DisasterResponse.ImOkay)
                            }
                        ) {
                            Text("I'm Okay")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onDisasterResponse(DisasterResponse.NeedHelp)
                            }
                        ) {
                            Text("Need Help")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SOSButton(
    isSOSAvailable: Boolean = true,
    isSOSActive: Boolean = false,
    onSOSClick: () -> Unit = {}
) {
    Button(
        onClick = onSOSClick,
        enabled = isSOSAvailable,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
            .alpha(if (isSOSAvailable) 1f else 0.75f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isSOSActive -> Gray
                else -> Red
            },
            contentColor = Color.White,
            disabledContainerColor = Red,
            disabledContentColor = Color.White
        )
    ) {
        Text(
            text = when {
                !isSOSAvailable -> "SOS Unavailable"
                isSOSActive -> "Stop SOS"
                else -> "Send SOS"
            },
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContentViewPreview() {
    IgathaTheme {
        ContentView(
            isSOSAvailable = true,
            isSOSActive = false,
            devices = listOf(
                Device(
                    id = UUID.randomUUID(),
                    rssi = -75.0,
                    lastSeen = Date()
                ),
                Device(
                    id = UUID.randomUUID(),
                    rssi = -85.0,
                    lastSeen = Date()
                )
            ),
            activeAlert = null,
            onSOSClick = {},
            onConfirmSOS = {},
            onDismissAlert = {},
            onDisasterResponse = {},
            onSettingsClick = {},
            onDeviceClick = { device -> }
        )
    }
}
