package moe.pyropix.ui.mine

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
        topBar = {
            TopAppBar(
                title = { Text("我的") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MineCard(
                icon = { Icon(Icons.Rounded.History, "历史", tint = GraphBlue) },
                title = "历史记录",
                count = history.size,
                gradient = listOf(GraphBlue, GraphBlue.copy(alpha = 0.6f)),
                onClick = { navCtrl.navigate(Routes.HISTORY) }
            )
            MineCard(
                icon = { Icon(Icons.Rounded.Star, "收藏", tint = RealtimeYellow) },
                title = "我的收藏",
                count = favs.size,
                gradient = listOf(RealtimeYellow, RealtimeYellow.copy(alpha = 0.6f)),
                onClick = { navCtrl.navigate(Routes.FAV) }
            )
            MineCard(
                icon = { Icon(Icons.Rounded.AutoAwesome, "模板", tint = MdPurple) },
                title = "公式模板",
                count = templates.sumOf { it.formulas.size },
                gradient = listOf(MdPurple, MdPurple.copy(alpha = 0.6f)),
                onClick = { navCtrl.navigate(Routes.TEMPLATE) }
            )
            MineCard(
                icon = { Icon(Icons.Rounded.Settings, "设置", tint = TxtGray) },
                title = "设置",
                count = null,
                gradient = listOf(TxtGray, TxtGray.copy(alpha = 0.6f)),
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
    gradient: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                onClick = onClick,
                onClickLabel = title,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(gradient.map { it.copy(alpha = 0.08f) }))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(gradient.map { it.copy(alpha = 0.15f) })),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(Modifier.width(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (count != null) {
                Badge(
                    containerColor = gradient.first(),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("$count", color = SurfaceLight)
                }
            }
            Icon(Icons.Rounded.ChevronRight, "进入", tint = TxtGray)
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}
