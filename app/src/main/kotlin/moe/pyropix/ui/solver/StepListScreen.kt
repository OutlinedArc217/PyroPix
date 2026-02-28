package moe.pyropix.ui.solver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.LatexView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepListScreen(navCtrl: NavController, vm: SolverVM = hiltViewModel()) {
    val steps by vm.steps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("求解步骤") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "返回", tint = Brand)
                    }
                }
            )
        }
    ) { pad ->
        if (steps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(pad),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("暂无步骤", color = TxtGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(steps) { idx, step ->
                    val isLast = idx == steps.lastIndex
                    StepCard(idx + 1, step.label, step.latex, isLast)
                }
            }
        }
    }
}

@Composable
private fun StepCard(num: Int, label: String, latex: String, isLast: Boolean) {
    var expanded by remember { mutableStateOf(isLast) }
    val borderColor = if (isLast) LatexGreen else StepCyan

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(if (isLast) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLast)
                LatexGreen.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = borderColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            " $num ",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = borderColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.titleMedium)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        "展开",
                        tint = borderColor
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                LatexView(
                    latex = latex,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp, max = 180.dp)
                )
            }
        }
    }
}
