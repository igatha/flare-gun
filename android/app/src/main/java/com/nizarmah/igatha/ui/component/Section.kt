package com.nizarmah.igatha.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Section(
    header: String? = null,
    footer: String? = null,
    padding: Modifier = Modifier.padding(vertical = 8.dp),
    content: @Composable () -> Unit,
) {
    Column(modifier = padding) {
        header?.let {
            SectionHeader(text = it)
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 0.dp)
        ) {
            Column {
                content()
            }
        }

        footer?.let {
            SectionFooter(text = it)
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.bodySmall.copy(
            lineHeight = MaterialTheme.typography.bodySmall.fontSize.times(1.4f)
        ),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
    )
}

@Composable
fun SectionFooter(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            lineHeight = MaterialTheme.typography.bodySmall.fontSize.times(1.4f)
        ),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
    )
}

@Composable
fun SectionItem(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()
    }
}
