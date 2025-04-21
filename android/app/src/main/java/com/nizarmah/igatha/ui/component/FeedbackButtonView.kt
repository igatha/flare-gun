package com.nizarmah.igatha.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nizarmah.igatha.ui.theme.IgathaTheme
import com.nizarmah.igatha.ui.theme.colors

/**
 * A styled button for navigation to the feedback form.
 * Matches the iOS design with gradient background and heart icon.
 */
@Composable
fun FeedbackButtonView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Create a pink-to-purple gradient to match iOS
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colors.pink.copy(alpha = 0.6f),
            MaterialTheme.colors.purple.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(gradient)
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Heart outline icon
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Feedback",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )

                // Text content
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Tell us why you use Igatha",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "It helps us make it more reliable.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to feedback form",
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackButtonViewPreview() {
    IgathaTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            FeedbackButtonView(
                onClick = {}
            )
        }
    }
}
