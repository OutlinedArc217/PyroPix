package moe.pyropix.doc

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordHandler @Inject constructor() {

    suspend fun extractText(ctx: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val input = ctx.contentResolver.openInputStream(uri) ?: return@withContext ""
        val doc = XWPFDocument(input)
        val sb = StringBuilder()
        doc.paragraphs.forEach { p -> sb.appendLine(p.text) }
        doc.close()
        input.close()
        sb.toString()
    }

    suspend fun extractFormulas(ctx: Context, uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val formulas = mutableListOf<String>()
        val input = ctx.contentResolver.openInputStream(uri) ?: return@withContext formulas
        val doc = XWPFDocument(input)
        doc.paragraphs.forEach { p ->
            p.runs.forEach { run ->
                val xml = run.ctr.xmlText()
                if (xml.contains("oMath") || xml.contains("m:oMath")) {
                    formulas.add(ommlToLatex(xml))
                }
            }
        }
        doc.close()
        input.close()
        formulas
    }

    private fun ommlToLatex(omml: String): String {
        // OMML -> LaTeX basic conversion
        var s = omml
        s = Regex("""<m:t[^>]*>(.+?)</m:t>""").replace(s) { it.groupValues[1] }
        s = Regex("""<[^>]+>""").replace(s, "")
        return s.trim()
    }
}
