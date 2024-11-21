package com.nizarmah.igatha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nizarmah.igatha.ui.component.PermissionHandler
import com.nizarmah.igatha.ui.screen.ContentScreen
import com.nizarmah.igatha.ui.theme.IgathaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IgathaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(
        bottomBar = {
            PermissionHandler(
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { paddingValues ->
            ContentScreen(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainActivity()
}