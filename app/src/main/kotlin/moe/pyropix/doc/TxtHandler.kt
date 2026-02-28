package moe.pyropix.doc

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.pyropix.util.LatexUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TxtHandler @Inject constructor() {

    suspend fun readText(ctx: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
    }

    suspend fun writeText(ctx: Context, uri: Uri, text: String) = withContext(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use { it.write(text) }
    }

    suspend fun extractFormulas(ctx: Context, uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val text = readText(ctx, uri)
        LatexUtil.extractInline(text)
    }
}
