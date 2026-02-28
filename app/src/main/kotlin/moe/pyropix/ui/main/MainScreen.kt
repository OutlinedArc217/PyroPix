package moe.pyropix.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import moe.pyropix.ui.recognize.RecognizeScreen
import moe.pyropix.ui.doc.DocListScreen
import moe.pyropix.ui.solver.SolverScreen
import moe.pyropix.ui.mine.MineScreen
import moe.pyropix.ui.theme.*

data class TabItem(val label: String, val icon: ImageVector, val color: androidx.compose.ui.graphics.Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navCtrl: NavHostController) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        TabItem("识别", Icons.Rounded.CameraAlt, Brand),
        TabItem("文档", Icons.Rounded.Folder, WordBlue),
        TabItem("求解", Icons.Rounded.Calculate, SolveOrange),
        TabItem("我的", Icons.Rounded.Person, MdPurple)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(t.icon, t.label, tint = if (tab == i) t.color else TxtGray) },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                0 -> RecognizeScreen(navCtrl)
                1 -> DocListScreen(navCtrl)
                2 -> SolverScreen(navCtrl)
                3 -> MineScreen(navCtrl)
            }
        }
    }
}
