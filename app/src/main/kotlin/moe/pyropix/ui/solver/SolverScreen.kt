package moe.pyropix.ui.solver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.solver.SolveType
import moe.pyropix.ui.nav.Routes
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.LatexView
import moe.pyropix.ui.widget.SymbolPad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolverScreen(navCtrl: NavController, vm: SolverVM = hiltViewModel()) {
    val input by vm.inputLatex.collectAsState()
    val result by vm.result.collectAsState()
    val loading by vm.isLoading.collectAsState()
    var showPad by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("求解器") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { vm.setInput(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("LaTeX 公式") },
                        placeholder = { Text("输入公式，如 x^2 + 2x + 1") },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { showPad = !showPad }) {
                                Icon(Icons.Rounded.Keyboard, "符号", tint = StepCyan)
                            }
                        }
                    )
                }
            }

            if (showPad) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    SymbolPad(onInsert = { vm.setInput(input + it) })
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("运算", style = MaterialTheme.typography.titleMedium)
                    OpButtons { vm.solve(it) }
                }
            }

            if (loading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Brand)
                }
            }

            result?.let { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            if (res.success) "结果" else "错误",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (res.success) LatexGreen else PdfRed
                        )
                        if (res.success) {
                            LatexView(
                                latex = res.latex,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp, max = 200.dp)
                            )
                        } else {
                            Text(res.raw, color = PdfRed, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = { navCtrl.navigate(Routes.STEP_LIST) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = StepCyan.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(Icons.Rounded.FormatListNumbered, "步骤", tint = StepCyan)
                        Spacer(Modifier.width(8.dp))
                        Text("查看步骤", color = StepCyan)
                    }
                    FilledTonalButton(
                        onClick = {
                            vm.plotGraph(input)
                            val encoded = android.net.Uri.encode(input)
                            navCtrl.navigate(Routes.GRAPH.replace("{expr}", encoded))
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = GraphBlue.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ShowChart, "图像", tint = GraphBlue)
                        Spacer(Modifier.width(8.dp))
                        Text("函数图像", color = GraphBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun OpButtons(onSolve: (SolveType) -> Unit) {
    val ops = listOf(
        Triple("化简", SolveType.SIMPLIFY, SolveOrange),
        Triple("展开", SolveType.EXPAND, GraphBlue),
        Triple("因式分解", SolveType.FACTOR, LatexGreen),
        Triple("解方程", SolveType.EQUATION, PdfRed),
        Triple("求导", SolveType.DIFFERENTIATE, StepCyan),
        Triple("积分", SolveType.INTEGRATE, MdPurple),
        Triple("极限", SolveType.LIMIT, TemplateBrown)
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ops.forEach { (label, type, color) ->
            FilledTonalButton(
                onClick = { onSolve(type) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = color.copy(alpha = 0.15f)
                )
            ) {
                Text(label, color = color)
            }
        }
    }
}
