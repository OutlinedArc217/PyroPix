package moe.pyropix.ui.mine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.ui.nav.Routes
import moe.pyropix.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(navCtrl: NavController, vm: MineVM = hiltViewModel()) {
    val history by vm.history.collectAsState()
    val favs by vm.favorites.collectAsState()
    val templates by vm.templates.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MineCard(
                icon = { Icon(Icons.Rounded.History, "历史", tint = GraphBlue) },
                title = "历史记录",
                count = history.size,
                onClick = { navCtrl.navigate(Routes.HISTORY) }
            )
            MineCard(
                icon = { Icon(Icons.Rounded.Star, "收藏", tint = RealtimeYellow) },
                title = "我的收藏",
                count = favs.size,
                onClick = { navCtrl.navigate(Routes.FAV) }
            )
            MineCard(
                icon = { Icon(Icons.Rounded.AutoAwesome, "模板", tint = MdPurple) },
                title = "公式模板",
                count = templates.sumOf { it.formulas.size },
                onClick = { navCtrl.navigate(Routes.TEMPLATE) }
            )
            MineCard(
                icon = { Icon(Icons.Rounded.Settings, "设置", tint = TxtGray) },
                title = "设置",
                count = null,
                onClick = { navCtrl.navigate(Routes.SETTINGS) }
            )
        }
    }
}

@Composable
private fun MineCard(
    icon: @Composable () -> Unit,
    title: String,
    count: Int?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (count != null) {
                Badge(containerColor = Brand) {
                    Text("$count", color = SurfaceLight)
                }
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.ChevronRight, "进入", tint = TxtGray)
        }
    }
}
