package moe.pyropix.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.pyropix.ui.theme.*

@Composable
fun FormulaCard(
    latex: String,
    source: String,
    time: String,
    isFav: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSolve: () -> Unit,
    onFav: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            LatexView(
                latex = latex,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp, max = 200.dp)
            )

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = latex,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = moe.pyropix.ui.theme.MonoFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val srcColor = when (source) {
                        "camera" -> GraphBlue
                        "gallery" -> LatexGreen
                        "draw" -> SolveOrange
                        "text" -> StepCyan
                        "doc" -> MdPurple
                        else -> TxtGray
                    }
                    SuggestionChip(
                        onClick = {},
                        label = { Text(source, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = srcColor.copy(alpha = 0.12f)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(time, style = MaterialTheme.typography.labelSmall, color = TxtGray)
                }

                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "收起" else "展开")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onCopy) {
                    Icon(Icons.Rounded.ContentCopy, "copy", tint = StepCyan)
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Rounded.Share, "share", tint = GraphBlue)
                }
                IconButton(onClick = onSolve) {
                    Icon(Icons.Rounded.Calculate, "solve", tint = SolveOrange)
                }
                IconButton(onClick = onFav) {
                    Icon(
                        if (isFav) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        "fav",
                        tint = RealtimeYellow
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, "delete", tint = PdfRed)
                }
            }
        }
    }
}
