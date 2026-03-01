package moe.pyropix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import moe.pyropix.ui.nav.AppNav
import moe.pyropix.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        android.util.Log.d("MainActivity", "onCreate started")

        try {
            setContent {
                android.util.Log.d("MainActivity", "setContent called")
                AppTheme {
                    android.util.Log.d("MainActivity", "AppTheme started")
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        android.util.Log.d("MainActivity", "Creating NavController")
                        val navCtrl = rememberNavController()
                        android.util.Log.d("MainActivity", "Starting AppNav")
                        AppNav(navCtrl)
                    }
                }
            }
            android.util.Log.d("MainActivity", "onCreate completed successfully")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            throw e
        }
    }
}
