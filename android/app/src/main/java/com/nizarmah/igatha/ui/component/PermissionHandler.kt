package com.nizarmah.igatha.ui.component

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nizarmah.igatha.util.PermissionsHelper
import com.nizarmah.igatha.util.PermissionsManager

@Composable
fun PermissionHandler(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionsGranted by PermissionsManager.permissionsGranted.collectAsState()

    // Launcher to request multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            PermissionsManager.refreshPermissions(context)
        }
    )

    // Check Bluetooth status and prompt if disabled
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    // Request permissions on first launch
    LaunchedEffect(Unit) {
        if (!permissionsGranted) {
            permissionLauncher.launch(
                PermissionsHelper.getSOSPermissions()
                        + PermissionsHelper.getProximityScanPermissions()
                        + PermissionsHelper.getDisasterDetectionPermissions()
            )
        }
    }

    // Monitor Bluetooth state changes
    DisposableEffect(Unit) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    isBluetoothEnabled = state == BluetoothAdapter.STATE_ON
                }
            }
        }

        val filter = android.content.IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, filter)

        // Lifecycle observer for onResume
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh permissions and Bluetooth status
                PermissionsManager.refreshPermissions(context)
                isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            context.unregisterReceiver(receiver)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
    ) {
        if (!permissionsGranted) {
            PersistentBanner(
                message = "Permissions are missing.",
                actionLabel = "Settings",
                onActionClick = {
                    // Open app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    context.startActivity(intent)
                }
            )
        } else if (!isBluetoothEnabled) {
            PersistentBanner(
                message = "Bluetooth is required.",
                actionLabel = "Enable",
                onActionClick = {
                    // Open bluetooth settings
                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    context.startActivity(intent)
                }
            )
        }
    }
}
