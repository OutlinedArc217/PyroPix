package moe.pyropix.ui.recognize

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.CropOverlay
import moe.pyropix.ui.widget.LatexView
import moe.pyropix.util.BitmapUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(navCtrl: NavHostController, uri: String) {
    val vm: RecognizeVM = hiltViewModel()
    val latex by vm.recognizedLatex.collectAsState()
    val loading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    val decoded = Uri.decode(uri)
    val srcBitmap = remember { BitmapFactory.decodeFile(decoded) }

    if (srcBitmap == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("无法加载图片", color = PdfRed)
        }
        return
    }

    var viewW by remember { mutableIntStateOf(1) }
    var viewH by remember { mutableIntStateOf(1) }
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var recognized by remember { mutableStateOf(false) }

    LaunchedEffect(viewW, viewH) {
        if (viewW > 0 && viewH > 0) {
            val pad = 40f
            cropRect = Rect(pad, pad, viewW - pad, viewH - pad)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("裁剪") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "back", tint = Brand)
                    }
                },
                actions = {
                    if (!recognized) {
                        IconButton(onClick = { navCtrl.popBackStack() }) {
                            Icon(Icons.Rounded.Close, "cancel", tint = PdfRed)
                        }
                        IconButton(onClick = {
                            val scaleX = srcBitmap.width.toFloat() / viewW
                            val scaleY = srcBitmap.height.toFloat() / viewH
                            val rect = android.graphics.Rect(
                                (cropRect.left * scaleX).toInt(),
                                (cropRect.top * scaleY).toInt(),
                                (cropRect.right * scaleX).toInt(),
                                (cropRect.bottom * scaleY).toInt()
                            )
                            val cropped = BitmapUtil.crop(srcBitmap, rect)
                            vm.recognize(cropped)
                            recognized = true
                        }) {
                            Icon(Icons.Rounded.Check, "confirm", tint = LatexGreen)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (!recognized) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .onSizeChanged { viewW = it.width; viewH = it.height }
                ) {
                    Image(
                        bitmap = srcBitmap.asImageBitmap(),
                        contentDescription = "captured",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    CropOverlay(
                        cropRect = cropRect,
                        onCropChange = { cropRect = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Brand)
                    }
                } else if (error != null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("识别失败: $error", color = PdfRed)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("识别结果", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        LatexView(
                            latex = latex,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 200.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            latex,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = MonoFamily,
                            color = TxtGray
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = {
                                recognized = false
                                vm.clearResult()
                            }) { Text("重新裁剪") }
                            Button(
                                onClick = {
                                    vm.saveFormula(latex, "camera")
                                    navCtrl.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Brand)
                            ) { Text("保存") }
                        }
                    }
                }
            }
        }
    }
}
