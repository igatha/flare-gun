package com.nizarmah.igatha.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nizarmah.igatha.model.Device
import com.nizarmah.igatha.ui.theme.IgathaTheme
import java.util.*

@Composable
fun DeviceRowView(device: Device, onClick: () -> Unit = {}) {
    val isStale = device.lastSeen.before(
        Date(System.currentTimeMillis() - 300_000)
    ) // 5 minutes ago

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }
            .alpha(if (isStale) 0.4f else 1.0f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Device Icon",
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
                .padding(5.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = device.shortName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%.1f meters away",
                    device.estimateDistance()),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Sharp.KeyboardArrowRight,
            contentDescription = "See device details",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DeviceRowViewPreview() {
    val previewDevice = Device(
        id = UUID.randomUUID(),
        rssi = -60.0
    )

    IgathaTheme {
        DeviceRowView(device = previewDevice)
    }
}
