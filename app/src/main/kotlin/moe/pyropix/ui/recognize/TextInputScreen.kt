package moe.pyropix.ui.recognize

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import moe.pyropix.ui.theme.*
import moe.pyropix.ui.widget.LatexView
import moe.pyropix.ui.widget.SymbolPad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputScreen(navCtrl: NavHostController) {
    val vm: RecognizeVM = hiltViewModel()
    var tfv by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文本输入") },
                navigationIcon = {
                    IconButton(onClick = { navCtrl.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "back", tint = Brand)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (tfv.text.isNotBlank()) {
                                vm.saveFormula(tfv.text, "text")
                                navCtrl.popBackStack()
                            }
                        },
                        enabled = tfv.text.isNotBlank()
                    ) {
                        Icon(Icons.Rounded.Save, "save", tint = if (tfv.text.isNotBlank()) LatexGreen else TxtGray)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = tfv,
                onValueChange = { tfv = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).heightIn(min = 120.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonoFamily),
                label = { Text("LaTeX 公式") },
                placeholder = { Text("输入 LaTeX 代码...") },
                maxLines = 8
            )

            SymbolPad(
                onInsert = { sym ->
                    val before = tfv.text.substring(0, tfv.selection.start)
                    val after = tfv.text.substring(tfv.selection.end)
                    val newText = before + sym + after
                    val cursor = before.length + sym.length
                    tfv = TextFieldValue(newText, TextRange(cursor))
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = TxtGray.copy(alpha = 0.3f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                if (tfv.text.isNotBlank()) {
                    LatexView(
                        latex = tfv.text,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        "预览区域",
                        color = TxtGray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
