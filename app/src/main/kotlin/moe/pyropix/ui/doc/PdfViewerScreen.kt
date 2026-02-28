package moe.pyropix.ui.doc

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import moe.pyropix.doc.PdfHandler
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.CropOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(navCtrl: NavController, uriStr: String, vm: DocVM = hiltViewModel()) {
    val ctx = LocalContext.current
    val uri = remember { Uri.parse(uriStr) }
    val pdfHandler = remember { PdfHandler() }
    val scope = rememberCoroutineScope()
    var page by remember { mutableIntStateOf(0) }
    var pageCount by remember { mutableIntStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cropMode by remember { mutableStateOf(false) }
    var cropRect by remember { mutableStateOf(Rect(100f, 100f, 500f, 500f)) }

    LaunchedEffect(uri) {
        pageCount = pdfHandler.pageCount(ctx, uri)
        bitmap = pdfHandler.renderPage(ctx, uri, 0)
    }

    fun loadPage(p: Int) {
        scope.launch {
            bitmap = pdfHandler.renderPage(ctx, uri, p)
            page = p
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF 查看器") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                },
                actions = {
                    IconButton(onClick = { cropMode = !cropMode }) {
                        Icon(
                            Icons.Rounded.Crop,
                            "裁剪",
                            tint = if (cropMode) PdfRed else TxtGray
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (page > 0) loadPage(page - 1) },
                        enabled = page > 0
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.NavigateBefore, "上一页", tint = GraphBlue)
                    }
                    Text(
                        "${page + 1} / $pageCount",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = { if (page < pageCount - 1) loadPage(page + 1) },
                        enabled = page < pageCount - 1
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.NavigateNext, "下一页", tint = GraphBlue)
                    }
                }
            }
        }
    ) { pad ->
        Box(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentAlignment = Alignment.Center
        ) {
            val bmp = bitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "PDF 页面",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                if (cropMode) {
                    CropOverlay(
                        cropRect = cropRect,
                        onCropChange = { cropRect = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                CircularProgressIndicator(color = Brand)
            }
        }
    }
}
