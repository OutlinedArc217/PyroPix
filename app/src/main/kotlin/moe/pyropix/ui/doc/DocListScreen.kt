package moe.pyropix.ui.doc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import moe.pyropix.R
import moe.pyropix.doc.DocType
import moe.pyropix.ui.nav.Routes
import moe.pyropix.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocListScreen(navCtrl: NavController, vm: DocVM = hiltViewModel()) {
    val ctx = LocalContext.current
    val docs by vm.docList.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val cursor = ctx.contentResolver.query(uri, null, null, null, null)
        var name = "unknown"
        var size = 0L
        cursor?.use {
            if (it.moveToFirst()) {
                val ni = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val si = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (ni >= 0) name = it.getString(ni) ?: "unknown"
                if (si >= 0) size = it.getLong(si)
            }
        }
        val mime = ctx.contentResolver.getType(uri)
        val type = when {
            mime == "application/pdf" || name.endsWith(".pdf") -> DocType.PDF
            mime?.contains("wordprocessingml") == true || name.endsWith(".docx") -> DocType.WORD
            name.endsWith(".md") || name.endsWith(".markdown") -> DocType.MARKDOWN
            else -> DocType.TXT
        }
        vm.addDoc(DocItem(uri.toString(), name, type, size))
        val encoded = Uri.encode(uri.toString())
        when (type) {
            DocType.PDF -> navCtrl.navigate(Routes.PDF_VIEWER.replace("{uri}", encoded))
            else -> navCtrl.navigate(Routes.DOC_EDITOR.replace("{uri}", encoded))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文档") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { launcher.launch(arrayOf("*/*")) },
                containerColor = Brand,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, "打开文档", tint = SurfaceLight)
            }
        }
    ) { pad ->
        if (docs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Text("暂无文档，点击右下角打开", color = TxtGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(docs, key = { it.uri }) { doc ->
                    DocRow(doc) {
                        val encoded = Uri.encode(doc.uri)
                        when (doc.type) {
                            DocType.PDF -> navCtrl.navigate(Routes.PDF_VIEWER.replace("{uri}", encoded))
                            else -> navCtrl.navigate(Routes.DOC_EDITOR.replace("{uri}", encoded))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocRow(doc: DocItem, onClick: () -> Unit) {
    val iconRes = when (doc.type) {
        DocType.PDF -> R.drawable.ic_pdf
        DocType.WORD -> R.drawable.ic_word
        DocType.MARKDOWN -> R.drawable.ic_markdown
        DocType.TXT -> R.drawable.ic_txt
    }
    val tint = when (doc.type) {
        DocType.PDF -> PdfRed
        DocType.WORD -> WordBlue
        DocType.MARKDOWN -> MdPurple
        DocType.TXT -> TxtGray
    }
    val gradient = when (doc.type) {
        DocType.PDF -> listOf(PdfRed, PdfRed.copy(alpha = 0.6f))
        DocType.WORD -> listOf(WordBlue, WordBlue.copy(alpha = 0.6f))
        DocType.MARKDOWN -> listOf(MdPurple, MdPurple.copy(alpha = 0.6f))
        DocType.TXT -> listOf(TxtGray, TxtGray.copy(alpha = 0.6f))
    }

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                onClick = onClick,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(gradient.map { it.copy(alpha = 0.08f) }))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(gradient.map { it.copy(alpha = 0.15f) })),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(iconRes),
                    doc.type.name,
                    tint = tint,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    formatSize(doc.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = TxtGray
                )
            }
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    else -> "${"%.1f".format(bytes / 1048576.0)} MB"
}
