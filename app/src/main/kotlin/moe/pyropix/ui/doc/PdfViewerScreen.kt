package moe.pyropix.ui.doc

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(navCtrl: NavHostController, uri: String = "") {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PdfViewer") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("功能开发中...")
        }
    }
}
