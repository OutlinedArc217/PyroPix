package moe.pyropix.doc

import android.content.Context
import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocManager @Inject constructor(
    private val pdfHandler: PdfHandler,
    private val wordHandler: WordHandler,
    private val mdHandler: MdHandler,
    private val txtHandler: TxtHandler
) {
    fun detect(uri: Uri, mime: String?): DocType {
        val path = uri.path?.lowercase() ?: ""
        return when {
            mime == "application/pdf" || path.endsWith(".pdf") -> DocType.PDF
            mime == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" || path.endsWith(".docx") -> DocType.WORD
            path.endsWith(".md") || path.endsWith(".markdown") -> DocType.MARKDOWN
            else -> DocType.TXT
        }
    }

    suspend fun readText(ctx: Context, uri: Uri, type: DocType): String = when (type) {
        DocType.PDF -> pdfHandler.extractText(ctx, uri)
        DocType.WORD -> wordHandler.extractText(ctx, uri)
        DocType.MARKDOWN -> mdHandler.readText(ctx, uri)
        DocType.TXT -> txtHandler.readText(ctx, uri)
    }

    suspend fun extractFormulas(ctx: Context, uri: Uri, type: DocType): List<String> = when (type) {
        DocType.PDF -> emptyList()
        DocType.WORD -> wordHandler.extractFormulas(ctx, uri)
        DocType.MARKDOWN -> mdHandler.extractFormulas(ctx, uri)
        DocType.TXT -> txtHandler.extractFormulas(ctx, uri)
    }
}

enum class DocType { PDF, WORD, MARKDOWN, TXT }
