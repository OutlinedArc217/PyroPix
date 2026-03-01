package moe.pyropix.ui.recognize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.DrawCanvas
import moe.pyropix.ui.widget.DrawPath
import moe.pyropix.ui.widget.LatexView
import moe.pyropix.ui.widget.canvasToBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(navCtrl: NavHostController) {
    val vm: RecognizeVM = hiltViewModel()
    val latex by vm.recognizedLatex.collectAsState()
    val loading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    var paths by remember { mutableStateOf<List<DrawPath>>(emptyList()) }
    var undone by remember { mutableStateOf<List<DrawPath>>(emptyList()) }
    var curPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isEraser by remember { mutableStateOf(false) }
    var penLevel by remember { mutableIntStateOf(1) }
    val penWidth = when (penLevel) { 0 -> 4f; 1 -> 8f; else -> 16f }
    var recognized by remember { mutableStateOf(false) }
    var canvasW by remember { mutableIntStateOf(0) }
    var canvasH by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("手写识别") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "back", tint = Brand)
                    }
                },
                actions = {
                    IconButton(onClick = { penLevel = (penLevel + 1) % 3 }) {
                        Icon(Icons.Rounded.LineWeight, "pen", tint = GraphBlue)
                    }
                    IconButton(onClick = { isEraser = !isEraser }) {
                        Icon(
                            Icons.Rounded.AutoFixNormal,
                            "eraser",
                            tint = if (isEraser) PdfRed else TxtGray
                        )
                    }
                    IconButton(
                        onClick = {
                            if (paths.isNotEmpty()) {
                                undone = undone + paths.last()
                                paths = paths.dropLast(1)
                            }
                        }
                    ) { Icon(Icons.AutoMirrored.Rounded.Undo, "undo", tint = SolveOrange) }
                    IconButton(
                        onClick = {
                            if (undone.isNotEmpty()) {
                                paths = paths + undone.last()
                                undone = undone.dropLast(1)
                            }
                        }
                    ) { Icon(Icons.AutoMirrored.Rounded.Redo, "redo", tint = SolveOrange) }
                    IconButton(onClick = {
                        paths = emptyList()
                        undone = emptyList()
                        vm.clearResult()
                        recognized = false
                    }) { Icon(Icons.Rounded.DeleteSweep, "clear", tint = PdfRed) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!recognized) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White)
                        .onSizeChanged { canvasW = it.width; canvasH = it.height }
                ) {
                    DrawCanvas(
                        paths = paths,
                        currentPath = curPoints,
                        penColor = Color.Black,
                        penWidth = penWidth,
                        isEraser = isEraser,
                        onDragStart = { curPoints = listOf(it) },
                        onDrag = { curPoints = curPoints + it },
                        onDragEnd = {
                            paths = paths + DrawPath(curPoints, Color.Black, penWidth, isEraser)
                            curPoints = emptyList()
                            undone = emptyList()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Button(
                    onClick = {
                        if (canvasW > 0 && canvasH > 0) {
                            val bmp = canvasToBitmap(canvasW, canvasH, paths)
                            vm.recognize(bmp)
                            recognized = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand),
                    enabled = paths.isNotEmpty()
                ) { Text("识别") }
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
                        Text(latex, style = MaterialTheme.typography.bodySmall, fontFamily = MonoFamily, color = TxtGray)
                        Spacer(Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = {
                                recognized = false
                                vm.clearResult()
                            }) { Text("重新绘制") }
                            Button(
                                onClick = {
                                    vm.saveFormula(latex, "draw")
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
