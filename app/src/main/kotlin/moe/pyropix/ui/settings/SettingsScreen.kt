package moe.pyropix.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navCtrl: NavController, vm: SettingsVM = hiltViewModel()) {
    val precision by vm.precision.collectAsState()
    val autoCrop by vm.autoCrop.collectAsState()
    val outputFmt by vm.outputFmt.collectAsState()
    val fontSize by vm.fontSize.collectAsState()
    val cardLayout by vm.cardLayout.collectAsState()
    val threadCount by vm.threadCount.collectAsState()
    val useGpu by vm.useGpu.collectAsState()
    val penColor by vm.penColor.collectAsState()
    val penWidth by vm.penWidth.collectAsState()
    val autoDelay by vm.autoDelay.collectAsState()
    val palmReject by vm.palmReject.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SectionHeader("识别", Icons.Rounded.CameraAlt, GraphBlue)
            PrecisionDropdown(precision) { vm.setPrecision(it) }
            SwitchItem("自动裁剪", autoCrop) { vm.setAutoCrop(it) }
            OutputFmtDropdown(outputFmt) { vm.setOutputFmt(it) }

            Spacer(Modifier.height(8.dp))
            SectionHeader("外观", Icons.Rounded.Palette, MdPurple)
            SliderItem("字体大小", fontSize, 10f, 32f) { vm.setFontSize(it) }
            SwitchItem("卡片布局", cardLayout == "grid") {
                vm.setCardLayout(if (it) "grid" else "list")
            }

            Spacer(Modifier.height(8.dp))
            SectionHeader("存储", Icons.Rounded.Storage, BackupBlueGray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { vm.backup() }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.Backup, "备份", tint = ExportGreen)
                    Spacer(Modifier.width(4.dp))
                    Text("备份")
                }
                OutlinedButton(onClick = { vm.restore() }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.Restore, "恢复", tint = SolveOrange)
                    Spacer(Modifier.width(4.dp))
                    Text("恢复")
                }
            }

            Spacer(Modifier.height(8.dp))
            SectionHeader("手写", Icons.Rounded.Draw, SolveOrange)
            PenColorPicker(penColor) { vm.setPenColor(it) }
            SliderItem("笔宽", penWidth, 1f, 12f) { vm.setPenWidth(it) }
            SliderItem("自动识别延迟", autoDelay.toFloat(), 200f, 2000f) {
                vm.setAutoDelay(it.toInt())
            }
            SwitchItem("防误触", palmReject) { vm.setPalmReject(it) }

            Spacer(Modifier.height(8.dp))
            SectionHeader("高级", Icons.Rounded.Tune, StepCyan)
            ListItem(
                headlineContent = { Text("模型状态") },
                supportingContent = {
                    Text(
                        if (vm.modelLoaded()) "已加载 (${vm.modelSize()})" else "未加载"
                    )
                },
                leadingContent = {
                    Icon(Icons.Rounded.Memory, "模型", tint = LatexGreen)
                }
            )
            SliderItem("线程数", threadCount.toFloat(), 1f, 8f) {
                vm.setThreadCount(it.toInt())
            }
            SwitchItem("GPU 加速", useGpu) { vm.setUseGpu(it) }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: androidx.compose.ui.graphics.Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(icon, title, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun SwitchItem(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onChange)
        }
    )
}

@Composable
private fun SliderItem(label: String, value: Float, min: Float, max: Float, onChange: (Float) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                if (value == value.toInt().toFloat()) "${value.toInt()}" else "${"%.1f".format(value)}",
                style = MaterialTheme.typography.bodySmall,
                color = TxtGray
            )
        }
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = min..max
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrecisionDropdown(current: String, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("fast" to "快速", "balanced" to "均衡", "accurate" to "精确")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        ListItem(
            headlineContent = { Text("识别精度") },
            supportingContent = { Text(options.first { it.first == current }.second) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onChange(key); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OutputFmtDropdown(current: String, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("raw" to "原始 LaTeX", "rendered" to "渲染图片", "both" to "两者")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        ListItem(
            headlineContent = { Text("输出格式") },
            supportingContent = { Text(options.first { it.first == current }.second) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onChange(key); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun PenColorPicker(current: Int, onChange: (Int) -> Unit) {
    val colors = listOf(
        0xFF000000.toInt() to "黑",
        0xFFE53935.toInt() to "红",
        0xFF1E88E5.toInt() to "蓝",
        0xFF43A047.toInt() to "绿",
        0xFFFB8C00.toInt() to "橙"
    )
    ListItem(
        headlineContent = { Text("画笔颜色") },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                colors.forEach { (c, _) ->
                    val color = androidx.compose.ui.graphics.Color(c)
                    val selected = current == c
                    FilledIconButton(
                        onClick = { onChange(c) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (selected) color else color.copy(alpha = 0.3f)
                        )
                    ) {
                        if (selected) {
                            Icon(
                                Icons.Rounded.Check,
                                "选中",
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}
