package moe.pyropix.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SymbolGroup(val name: String, val symbols: List<String>)

val defaultSymbols = listOf(
    SymbolGroup("分数/根号", listOf("\\frac{}{}", "\\sqrt{}", "\\sqrt[n]{}", "\\binom{n}{k}")),
    SymbolGroup("上下标", listOf("^{}", "_{}", "^{}_{}", "\\hat{}", "\\bar{}", "\\vec{}", "\\dot{}", "\\ddot{}")),
    SymbolGroup("运算符", listOf("+", "-", "\\times", "\\div", "\\pm", "\\mp", "\\cdot", "\\circ")),
    SymbolGroup("关系", listOf("=", "\\neq", "\\leq", "\\geq", "<", ">", "\\approx", "\\equiv", "\\sim")),
    SymbolGroup("希腊字母", listOf("\\alpha", "\\beta", "\\gamma", "\\delta", "\\epsilon", "\\theta", "\\lambda", "\\mu", "\\pi", "\\sigma", "\\phi", "\\omega", "\\Gamma", "\\Delta", "\\Sigma", "\\Omega")),
    SymbolGroup("微积分", listOf("\\int", "\\iint", "\\oint", "\\sum", "\\prod", "\\lim", "\\infty", "\\partial", "\\nabla", "\\frac{d}{dx}")),
    SymbolGroup("矩阵", listOf("\\begin{pmatrix}  \\end{pmatrix}", "\\begin{bmatrix}  \\end{bmatrix}", "\\begin{vmatrix}  \\end{vmatrix}", "\\det")),
    SymbolGroup("括号", listOf("\\left(  \\right)", "\\left[  \\right]", "\\left\\{  \\right\\}", "\\left|  \\right|")),
    SymbolGroup("集合", listOf("\\in", "\\notin", "\\subset", "\\supset", "\\cup", "\\cap", "\\emptyset", "\\forall", "\\exists")),
    SymbolGroup("箭头", listOf("\\to", "\\Rightarrow", "\\Leftrightarrow", "\\mapsto"))
)

@Composable
fun SymbolPad(
    onInsert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        ScrollableTabRow(selectedTabIndex = selectedTab) {
            defaultSymbols.forEachIndexed { i, g ->
                Tab(selected = selectedTab == i, onClick = { selectedTab = i }) {
                    Text(g.name, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        val group = defaultSymbols[selectedTab]
        LazyVerticalGrid(
            columns = GridCells.Adaptive(64.dp),
            modifier = Modifier.heightIn(max = 200.dp).padding(4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(group.symbols) { sym ->
                OutlinedButton(
                    onClick = { onInsert(sym) },
                    modifier = Modifier.padding(2.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    val display = sym.removePrefix("\\").take(6)
                    Text(display, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
        }
    }
}
