package moe.pyropix.ui.doc

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.doc.MdHandler
import moe.pyropix.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocEditorScreen(navCtrl: NavController, uriStr: String, vm: DocVM = hiltViewModel()) {
    val ctx = LocalContext.current
    val uri = remember { Uri.parse(uriStr) }
    val text by vm.currentText.collectAsState()
    val formulas by vm.extractedFormulas.collectAsState()
    var editText by remember { mutableStateOf("") }
    var fontSize by remember { mutableFloatStateOf(16f) }
    var showFind by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }
    var replaceWith by remember { mutableStateOf("") }
    var previewMd by remember { mutableStateOf(false) }
    var showFormulas by remember { mutableStateOf(false) }
    val undoStack = remember { mutableStateListOf<String>() }
    val redoStack = remember { mutableStateListOf<String>() }
    val isMd = remember { uriStr.contains(".md") || uriStr.contains(".markdown") }

    LaunchedEffect(uri) { vm.loadDoc(ctx, uri) }
    LaunchedEffect(text) { if (editText.isEmpty() && text.isNotEmpty()) editText = text }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑器") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        vm.saveText(ctx, uri, editText)
                    }) { Icon(Icons.Rounded.Save, "保存", tint = LatexGreen) }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            EditorToolbar(
                fontSize = fontSize,
                onFontSize = { fontSize = it },
                onFind = { showFind = !showFind },
                onUndo = {
                    if (undoStack.isNotEmpty()) {
                        redoStack.add(editText)
                        editText = undoStack.removeLast()
                    }
                },
                onRedo = {
                    if (redoStack.isNotEmpty()) {
                        undoStack.add(editText)
                        editText = redoStack.removeLast()
                    }
                },
                isMd = isMd,
                previewMd = previewMd,
                onTogglePreview = { previewMd = !previewMd },
                onExtract = {
                    vm.extractFormulas(ctx, uri)
                    showFormulas = true
                }
            )

            if (showFind) {
                FindReplaceBar(
                    query = findQuery,
                    onQueryChange = { findQuery = it },
                    replace = replaceWith,
                    onReplaceChange = { replaceWith = it },
                    onReplace = {
                        if (findQuery.isNotEmpty()) {
                            undoStack.add(editText)
                            editText = editText.replaceFirst(findQuery, replaceWith)
                        }
                    },
                    onReplaceAll = {
                        if (findQuery.isNotEmpty()) {
                            undoStack.add(editText)
                            editText = editText.replace(findQuery, replaceWith)
                        }
                    }
                )
            }

            if (showFormulas && formulas.isNotEmpty()) {
                FormulaChips(formulas) { formula ->
                    undoStack.add(editText)
                    editText += "\n$$${formula}$$\n"
                }
            }

            if (previewMd && isMd) {
                MdPreview(editText, Modifier.fillMaxSize())
            } else {
                OutlinedTextField(
                    value = editText,
                    onValueChange = {
                        undoStack.add(editText)
                        redoStack.clear()
                        editText = it
                    },
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize.sp),
                    placeholder = { Text("输入内容...") }
                )
            }
        }
    }
}

@Composable
private fun EditorToolbar(
    fontSize: Float,
    onFontSize: (Float) -> Unit,
    onFind: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    isMd: Boolean,
    previewMd: Boolean,
    onTogglePreview: () -> Unit,
    onExtract: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            IconButton(onClick = { onFontSize((fontSize - 2f).coerceAtLeast(10f)) }) {
                Icon(Icons.Rounded.TextDecrease, "缩小", tint = GraphBlue)
            }
            IconButton(onClick = { onFontSize((fontSize + 2f).coerceAtMost(32f)) }) {
                Icon(Icons.Rounded.TextIncrease, "放大", tint = GraphBlue)
            }
            IconButton(onClick = onFind) {
                Icon(Icons.Rounded.FindReplace, "查找", tint = StepCyan)
            }
            IconButton(onClick = onUndo) {
                Icon(Icons.AutoMirrored.Rounded.Undo, "撤销", tint = SolveOrange)
            }
            IconButton(onClick = onRedo) {
                Icon(Icons.AutoMirrored.Rounded.Redo, "重做", tint = SolveOrange)
            }
        }
        Row {
            if (isMd) {
                IconButton(onClick = onTogglePreview) {
                    Icon(
                        if (previewMd) Icons.Rounded.Edit else Icons.Rounded.Preview,
                        "预览",
                        tint = MdPurple
                    )
                }
            }
            IconButton(onClick = onExtract) {
                Icon(Icons.Rounded.Functions, "提取公式", tint = LatexGreen)
            }
        }
    }
}

@Composable
private fun FindReplaceBar(
    query: String,
    onQueryChange: (String) -> Unit,
    replace: String,
    onReplaceChange: (String) -> Unit,
    onReplace: () -> Unit,
    onReplaceAll: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(8.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("查找") },
            singleLine = true
        )
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = replace,
                onValueChange = onReplaceChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("替换为") },
                singleLine = true
            )
            TextButton(onClick = onReplace) { Text("替换") }
            TextButton(onClick = onReplaceAll) { Text("全部") }
        }
    }
}

@Composable
private fun FormulaChips(formulas: List<String>, onInsert: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        formulas.take(5).forEach { f ->
            AssistChip(
                onClick = { onInsert(f) },
                label = { Text(f.take(20), maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(containerColor = LatexGreen.copy(alpha = 0.12f))
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun MdPreview(md: String, modifier: Modifier = Modifier) {
    val mdHandler = remember { MdHandler() }
    val html = remember(md) { mdHandler.renderHtml(md) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
            }
        },
        update = { wv -> wv.loadData(html, "text/html", "utf-8") }
    )
}

private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
