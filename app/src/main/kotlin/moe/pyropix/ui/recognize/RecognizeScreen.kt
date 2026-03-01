package moe.pyropix.ui.recognize

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import moe.pyropix.R
import moe.pyropix.ui.nav.Routes
import moe.pyropix.ui.theme.*

private data class EntryCard(val label: String, val icon: Int, val gradient: List<Color>, val route: String)

private val entries = listOf(
    EntryCard("拍照识别", R.drawable.ic_camera_color, listOf(Color(0xFF1E88E5), Color(0xFF1565C0)), Routes.CAMERA),
    EntryCard("图片识别", R.drawable.ic_gallery_color, listOf(Color(0xFF43A047), Color(0xFF2E7D32)), Routes.GALLERY),
    EntryCard("手写识别", R.drawable.ic_draw_color, listOf(Color(0xFFFF6B35), Color(0xFFE55520)), Routes.DRAW),
    EntryCard("实时预览", R.drawable.ic_realtime_color, listOf(Color(0xFFFDD835), Color(0xFFF9A825)), Routes.LIVE_DRAW),
    EntryCard("文本输入", R.drawable.ic_keyboard_color, listOf(Color(0xFF00ACC1), Color(0xFF00838F)), Routes.TEXT_INPUT)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeScreen(navCtrl: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("公式识别", style = MaterialTheme.typography.headlineMedium) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(entries) { entry ->
                ModernEntryCard(entry) { navCtrl.navigate(entry.route) }
            }
        }
    }
}

@Composable
private fun ModernEntryCard(entry: EntryCard, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .scale(scale)
            .clickable {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(entry.gradient.map { it.copy(alpha = 0.1f) })
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(entry.gradient.map { it.copy(alpha = 0.2f) })
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(entry.icon),
                        contentDescription = entry.label,
                        tint = entry.gradient[0],
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    entry.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = entry.gradient[0]
                )
            }
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}
