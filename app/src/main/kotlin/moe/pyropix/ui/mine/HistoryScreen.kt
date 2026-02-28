package moe.pyropix.ui.mine

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.flow.flowOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.data.db.FormulaEntity
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.FormulaCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navCtrl: NavController, vm: MineVM = hiltViewModel()) {
    val history by vm.history.collectAsState()
    var query by remember { mutableStateOf("") }
    val searchFlow = remember(query) {
        if (query.isBlank()) null else vm.search(query)
    }
    val searchResult by (searchFlow ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .collectAsState(emptyList())
    val selected = remember { mutableStateListOf<Long>() }
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selected.isNotEmpty()) Text("已选 ${selected.size}")
                    else Text("历史记录")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selected.isNotEmpty()) selected.clear()
                        else navCtrl.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                },
                actions = {
                    if (selected.isNotEmpty()) {
                        IconButton(onClick = {
                            vm.deleteFormulas(selected.toList())
                            selected.clear()
                        }) {
                            Icon(Icons.Rounded.Delete, "删除", tint = PdfRed)
                        }
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("搜索公式...") },
                leadingIcon = { Icon(Icons.Rounded.Search, "搜索", tint = StepCyan) },
                singleLine = true
            )

            val items = if (query.isBlank()) history else searchResult
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无记录", color = TxtGray)
                }
            } else {
                HistoryList(
                    items = items,
                    selected = selected,
                    onToggle = { id ->
                        if (selected.contains(id)) selected.remove(id) else selected.add(id)
                    },
                    onCopy = { clipboard.setText(AnnotatedString(it.latex)) },
                    onFav = { vm.toggleFav(it) },
                    onDelete = { vm.deleteFormula(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryList(
    items: List<FormulaEntity>,
    selected: List<Long>,
    onToggle: (Long) -> Unit,
    onCopy: (FormulaEntity) -> Unit,
    onFav: (FormulaEntity) -> Unit,
    onDelete: (FormulaEntity) -> Unit
) {
    val fmt = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { f ->
            val isSelected = selected.contains(f.id)
            val bg by animateColorAsState(
                if (isSelected) Brand.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface,
                label = "sel"
            )

            Box(
                modifier = Modifier
                    .background(bg, MaterialTheme.shapes.medium)
                    .combinedClickable(
                        onClick = { if (selected.isNotEmpty()) onToggle(f.id) },
                        onLongClick = { onToggle(f.id) }
                    )
            ) {
                FormulaCard(
                    latex = f.latex,
                    source = f.source,
                    time = fmt.format(Date(f.createdAt)),
                    isFav = f.isFav,
                    onCopy = { onCopy(f) },
                    onShare = {},
                    onSolve = {},
                    onFav = { onFav(f) },
                    onDelete = { onDelete(f) }
                )
            }
        }
    }
}
