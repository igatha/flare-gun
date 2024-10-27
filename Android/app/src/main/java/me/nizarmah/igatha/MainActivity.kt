package me.nizarmah.igatha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.nizarmah.igatha.ui.screen.ContentScreen
import me.nizarmah.igatha.ui.theme.IgathaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IgathaTheme {
                ContentScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainActivity()
}