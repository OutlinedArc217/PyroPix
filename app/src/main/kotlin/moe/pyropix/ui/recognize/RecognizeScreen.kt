package moe.pyropix.ui.recognize

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import moe.pyropix.ui.nav.Routes

private data class RecognizeOption(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val options = listOf(
    RecognizeOption("拍照识别", Icons.Rounded.CameraAlt, Routes.CAMERA),
    RecognizeOption("图片识别", Icons.Rounded.Image, Routes.GALLERY),
    RecognizeOption("手写识别", Icons.Rounded.Draw, Routes.DRAW),
    RecognizeOption("实时预览", Icons.Rounded.Visibility, Routes.LIVE_DRAW),
    RecognizeOption("文本输入", Icons.Rounded.Keyboard, Routes.TEXT_INPUT)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeScreen(navCtrl: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("公式识别") }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options) { option ->
                Card(
                    onClick = { navCtrl.navigate(option.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            option.icon,
                            contentDescription = option.label,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            option.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
