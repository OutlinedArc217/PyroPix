package moe.pyropix.ui.mine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun FavScreen(navCtrl: NavController, vm: MineVM = hiltViewModel()) {
    val favs by vm.favorites.collectAsState()
    val groups by vm.groups.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddGroup by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current
    val fmt = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGroup = true },
                containerColor = Brand
            ) {
                Icon(Icons.Rounded.CreateNewFolder, "新建分组", tint = SurfaceLight)
            }
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            val tabs = listOf("全部") + groups.map { it.name }
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, name ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i }
                    ) {
                        Text(name, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            val filtered = if (selectedTab == 0) {
                favs
            } else {
                val gid = groups.getOrNull(selectedTab - 1)?.id
                favs.filter { it.groupId == gid }
            }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无收藏", color = TxtGray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { f ->
                        FormulaCard(
                            latex = f.latex,
                            source = f.source,
                            time = fmt.format(Date(f.createdAt)),
                            isFav = true,
                            onCopy = { clipboard.setText(AnnotatedString(f.latex)) },
                            onShare = {},
                            onSolve = {},
                            onFav = { vm.toggleFav(f) },
                            onDelete = { vm.deleteFormula(f) }
                        )
                    }
                }
            }
        }
    }

    if (showAddGroup) {
        AlertDialog(
            onDismissRequest = { showAddGroup = false },
            title = { Text("新建分组") },
            text = {
                OutlinedTextField(
                    value = newGroupName,
                    onValueChange = { newGroupName = it },
                    placeholder = { Text("分组名称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newGroupName.isNotBlank()) {
                        vm.addGroup(newGroupName.trim())
                        newGroupName = ""
                        showAddGroup = false
                    }
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showAddGroup = false }) { Text("取消") }
            }
        )
    }
}
