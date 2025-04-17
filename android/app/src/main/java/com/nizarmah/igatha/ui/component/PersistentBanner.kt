package com.nizarmah.igatha.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PersistentBanner(
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    backgroundColor: Color = Color(0xFF323232),
    messageColor: Color = Color.White,
    actionTextColor: Color = Color(0xFFBB86FC)
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 0.dp), // Minimal external padding
        color = backgroundColor, // Directly set the background color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Internal padding for content
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = messageColor
            )
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionLabel,
                    color = actionTextColor
                )
            }
        }
    }
}
