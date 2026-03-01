package moe.pyropix.ui.recognize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import kotlinx.coroutines.delay
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.DrawCanvas
import moe.pyropix.ui.widget.DrawPath
import moe.pyropix.ui.widget.LatexView
import moe.pyropix.ui.widget.canvasToBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDrawScreen(navCtrl: NavHostController) {
    val vm: RecognizeVM = hiltViewModel()
    val latex by vm.recognizedLatex.collectAsState()
    val loading by vm.isLoading.collectAsState()

    var paths by remember { mutableStateOf<List<DrawPath>>(emptyList()) }
    var curPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isEraser by remember { mutableStateOf(false) }
    var penWidth by remember { mutableFloatStateOf(8f) }
    var autoRecog by remember { mutableStateOf(true) }
    var drawVersion by remember { mutableLongStateOf(0L) }
    var canvasW by remember { mutableIntStateOf(0) }
    var canvasH by remember { mutableIntStateOf(0) }
    val delayMs = 800L

    // debounced auto-recognition
    LaunchedEffect(drawVersion, autoRecog) {
        if (!autoRecog || paths.isEmpty() || canvasW <= 0) return@LaunchedEffect
        delay(delayMs)
        val bmp = canvasToBitmap(canvasW, canvasH, paths)
        vm.recognize(bmp)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("实时预览") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "back", tint = Brand)
                    }
                },
                actions = {
                    IconButton(onClick = { isEraser = !isEraser }) {
                        Icon(
                            Icons.Rounded.AutoFixNormal,
                            "eraser",
                            tint = if (isEraser) PdfRed else TxtGray
                        )
                    }
                    IconButton(onClick = { autoRecog = !autoRecog }) {
                        Icon(
                            if (autoRecog) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                            "auto",
                            tint = if (autoRecog) LatexGreen else TxtGray
                        )
                    }
                    IconButton(onClick = {
                        paths = emptyList()
                        vm.clearResult()
                    }) { Icon(Icons.Rounded.DeleteSweep, "clear", tint = PdfRed) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                            drawVersion++
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Brand)
                    } else if (latex.isNotEmpty()) {
                        LatexView(
                            latex = latex,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("在上方绘制公式", color = TxtGray)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        if (canvasW > 0 && paths.isNotEmpty()) {
                            val bmp = canvasToBitmap(canvasW, canvasH, paths)
                            vm.recognize(bmp)
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = StepCyan.copy(alpha = 0.15f)
                    )
                ) {
                    Text("手动识别", color = StepCyan)
                }
                Button(
                    onClick = {
                        if (latex.isNotEmpty()) {
                            vm.saveFormula(latex, "draw")
                            navCtrl.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand),
                    enabled = latex.isNotEmpty()
                ) {
                    Text("保存")
                }
            }
        }
    }
}
