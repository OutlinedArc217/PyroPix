package moe.pyropix.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import moe.pyropix.ui.recognize.RecognizeScreen
import moe.pyropix.ui.doc.DocListScreen
import moe.pyropix.ui.solver.SolverScreen
import moe.pyropix.ui.mine.MineScreen

@Composable
fun MainScreen(navCtrl: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.CameraAlt, "识别") },
                    label = { Text("识别") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.Description, "文档") },
                    label = { Text("文档") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.Calculate, "求解") },
                    label = { Text("求解") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Person, "我的") },
                    label = { Text("我的") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> RecognizeScreen(navCtrl)
                1 -> DocListScreen(navCtrl)
                2 -> SolverScreen(navCtrl)
                3 -> MineScreen(navCtrl)
            }
        }
    }
}
