package moe.pyropix.ui.doc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocListScreen(navCtrl: NavHostController, uri: String = "") {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("文档") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Open file picker */ },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, "添加文档")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "暂无文档\n点击右下角添加",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
