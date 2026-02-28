package moe.pyropix.doc

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.pyropix.util.LatexUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MdHandler @Inject constructor() {

    suspend fun readText(ctx: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
    }

    suspend fun extractFormulas(ctx: Context, uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val text = readText(ctx, uri)
        LatexUtil.extractInline(text)
    }

    fun renderHtml(md: String): String {
        val parser = org.commonmark.parser.Parser.builder().build()
        val renderer = org.commonmark.renderer.html.HtmlRenderer.builder().build()
        val doc = parser.parse(md)
        return renderer.render(doc)
    }
}
