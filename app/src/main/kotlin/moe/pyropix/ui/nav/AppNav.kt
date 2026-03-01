package moe.pyropix.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import moe.pyropix.ui.main.MainScreen
import moe.pyropix.ui.recognize.*
import moe.pyropix.ui.doc.*
import moe.pyropix.ui.solver.*
import moe.pyropix.ui.mine.*
import moe.pyropix.ui.settings.SettingsScreen

@Composable
fun AppNav(navCtrl: NavHostController) {
    NavHost(navController = navCtrl, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) { MainScreen(navCtrl) }
        composable(Routes.CAMERA) { CameraScreen(navCtrl) }
        composable(Routes.CROP) { entry ->
            val uri = entry.arguments?.getString("uri") ?: ""
            CropScreen(navCtrl, uri)
        }
        composable(Routes.GALLERY) { GalleryScreen(navCtrl) }
        composable(Routes.DRAW) { DrawScreen(navCtrl) }
        composable(Routes.LIVE_DRAW) { LiveDrawScreen(navCtrl) }
        composable(Routes.TEXT_INPUT) { TextInputScreen(navCtrl) }
        composable(Routes.DOC_EDITOR) { entry ->
            val uri = entry.arguments?.getString("uri") ?: ""
            DocEditorScreen(navCtrl, uri)
        }
        composable(Routes.PDF_VIEWER) { entry ->
            val uri = entry.arguments?.getString("uri") ?: ""
            PdfViewerScreen(navCtrl, uri)
        }
        composable(Routes.STEP_LIST) { StepListScreen(navCtrl) }
        composable(Routes.GRAPH) { entry ->
            val expr = entry.arguments?.getString("expr") ?: ""
            GraphScreen(navCtrl, expr)
        }
        composable(Routes.HISTORY) { HistoryScreen(navCtrl) }
        composable(Routes.FAV) { FavScreen(navCtrl) }
        composable(Routes.TEMPLATE) { TemplateScreen(navCtrl) }
        composable(Routes.SETTINGS) { SettingsScreen(navCtrl) }
    }
}
