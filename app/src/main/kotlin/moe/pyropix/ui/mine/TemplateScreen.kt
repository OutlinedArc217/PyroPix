package moe.pyropix.ui.mine

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.LatexView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen(navCtrl: NavController, vm: MineVM = hiltViewModel()) {
    val templates by vm.templates.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val clipboard = LocalClipboardManager.current
    var editFormula by remember { mutableStateOf<String?>(null) }
    var editText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("公式模板") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            if (templates.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无模板", color = TxtGray)
                }
            } else {
                ScrollableTabRow(selectedTabIndex = selectedTab) {
                    templates.forEachIndexed { i, cat ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i }
                        ) {
                            Text(cat.name, modifier = Modifier.padding(12.dp))
                        }
                    }
                }

                val formulas = templates.getOrNull(selectedTab)?.formulas ?: emptyList()
                TemplateGrid(
                    formulas = formulas,
                    onCopy = { clipboard.setText(AnnotatedString(it.latex)) },
                    onEdit = { editFormula = it.latex; editText = it.latex }
                )
            }
        }
    }

    editFormula?.let { _ ->
        AlertDialog(
            onDismissRequest = { editFormula = null },
            title = { Text("编辑并保存") },
            text = {
                Column {
                    LatexView(
                        latex = editText,
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.saveFormula(editText)
                    editFormula = null
                }) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { editFormula = null }) { Text("取消") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TemplateGrid(
    formulas: List<TemplateFormula>,
    onCopy: (TemplateFormula) -> Unit,
    onEdit: (TemplateFormula) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(formulas) { f ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onCopy(f) },
                        onLongClick = { onEdit(f) }
                    ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text(f.name, style = MaterialTheme.typography.labelSmall, color = TxtGray)
                    LatexView(
                        latex = f.latex,
                        modifier = Modifier.fillMaxWidth().height(70.dp),
                        fontSize = 14f
                    )
                }
            }
        }
    }
}
