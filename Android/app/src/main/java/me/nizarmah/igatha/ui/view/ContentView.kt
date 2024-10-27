package me.nizarmah.igatha.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import me.nizarmah.igatha.model.Device
import me.nizarmah.igatha.ui.screen.SettingsScreen

@Composable
fun ContentView() {
    val navController = rememberNavController()
    val gson = remember { Gson() }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeContent(
                onDeviceClick = { device ->
                    val deviceJson = gson.toJson(device)
                    navController.navigate("device_detail/${deviceJson}")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "device_detail/{deviceJson}",
            arguments = listOf(navArgument("deviceJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceJson = backStackEntry.arguments?.getString("deviceJson")
            val device = gson.fromJson(deviceJson, Device::class.java)
            DeviceDetailView(device, onBackClick = {
                navController.popBackStack()
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    onDeviceClick: (Device) -> Unit,
    onSettingsClick: () -> Unit
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
                    devices = emptyList(), // Replace with actual device list when available
                    onDeviceClick = onDeviceClick
                )
            }

            SOSButton(
                isSOSAvailable = true,
                isSOSActive = false,
                onSOSClick = {
                    // TODO: Implement SOS button logic
                }
            )
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
        ContentView()
    }
}
