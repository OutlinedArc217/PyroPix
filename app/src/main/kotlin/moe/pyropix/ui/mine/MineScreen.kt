package moe.pyropix.ui.mine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import moe.pyropix.ui.nav.Routes

private data class MenuItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

private val menuItems = listOf(
    MenuItem("历史记录", Icons.Rounded.History, Routes.HISTORY),
    MenuItem("我的收藏", Icons.Rounded.Star, Routes.FAV),
    MenuItem("公式模板", Icons.Rounded.AutoAwesome, Routes.TEMPLATE),
    MenuItem("设置", Icons.Rounded.Settings, Routes.SETTINGS)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(navCtrl: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("我的") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            menuItems.forEach { item ->
                Card(
                    onClick = { navCtrl.navigate(item.route) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            item.label,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
