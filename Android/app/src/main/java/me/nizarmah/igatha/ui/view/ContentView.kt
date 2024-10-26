package me.nizarmah.igatha.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import me.nizarmah.igatha.ui.theme.Gray
import me.nizarmah.igatha.ui.theme.IgathaTheme
import me.nizarmah.igatha.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView() {
    // TODO: Initialize your ViewModel here when ready

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = {
                        // TODO: Implement navigation to SettingsView
                    }) {
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
        },
        content = { paddingValues ->
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
                    DeviceListView(devices = emptyList())
                }

                Spacer(modifier = Modifier)

                SOSButton(
                    isSOSAvailable = true,
                    isSOSActive = false,
                    onSOSClick = {
                        // TODO: Implement SOS button logic
                    }
                )
            }
        }
    )
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
        ContentView()
    }
}
