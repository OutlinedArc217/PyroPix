package moe.pyropix.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import moe.pyropix.ui.nav.AppNav
import moe.pyropix.ui.theme.PyroTheme
import moe.pyropix.util.I18n
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var i18n: I18n

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        i18n.load(this)
        setContent {
            PyroTheme {
                val navCtrl = rememberNavController()
                AppNav(navCtrl)
            }
        }
    }
}
