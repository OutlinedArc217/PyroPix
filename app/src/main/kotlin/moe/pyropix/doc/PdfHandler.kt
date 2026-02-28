package moe.pyropix.doc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfHandler @Inject constructor() {

    suspend fun extractText(ctx: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        PDFBoxResourceLoader.init(ctx)
        val input = ctx.contentResolver.openInputStream(uri) ?: return@withContext ""
        val doc = PDDocument.load(input)
        val text = PDFTextStripper().getText(doc)
        doc.close()
        input.close()
        text
    }

    suspend fun pageCount(ctx: Context, uri: Uri): Int = withContext(Dispatchers.IO) {
        val fd = ctx.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext 0
        val renderer = PdfRenderer(fd)
        val count = renderer.pageCount
        renderer.close()
        fd.close()
        count
    }

    suspend fun renderPage(ctx: Context, uri: Uri, page: Int, width: Int = 1080): Bitmap? = withContext(Dispatchers.IO) {
        val fd = ctx.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext null
        val renderer = PdfRenderer(fd)
        if (page >= renderer.pageCount) { renderer.close(); fd.close(); return@withContext null }
        val p = renderer.openPage(page)
        val scale = width.toFloat() / p.width
        val h = (p.height * scale).toInt()
        val bmp = Bitmap.createBitmap(width, h, Bitmap.Config.ARGB_8888)
        p.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        p.close()
        renderer.close()
        fd.close()
        bmp
    }
}
