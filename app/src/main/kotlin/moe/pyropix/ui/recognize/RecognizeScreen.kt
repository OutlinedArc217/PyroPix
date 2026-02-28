package moe.pyropix.ui.recognize

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import moe.pyropix.R
import moe.pyropix.ui.nav.Routes
import moe.pyropix.ui.theme.*

private data class EntryCard(val label: String, val icon: Int, val tint: Color, val route: String)

private val entries = listOf(
    EntryCard("拍照识别", R.drawable.ic_camera_color, Brand, Routes.CAMERA),
    EntryCard("图片识别", R.drawable.ic_gallery_color, LatexGreen, Routes.GALLERY),
    EntryCard("手写识别", R.drawable.ic_draw_color, SolveOrange, Routes.DRAW),
    EntryCard("实时预览", R.drawable.ic_realtime_color, RealtimeYellow, Routes.LIVE_DRAW),
    EntryCard("文本输入", R.drawable.ic_keyboard_color, StepCyan, Routes.TEXT_INPUT)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeScreen(navCtrl: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("公式识别") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .clickable { navCtrl.navigate(entry.route) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = entry.tint.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(entry.icon),
                            contentDescription = entry.label,
                            tint = entry.tint,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            entry.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = entry.tint
                        )
                    }
                }
            }
        }
    }
}
