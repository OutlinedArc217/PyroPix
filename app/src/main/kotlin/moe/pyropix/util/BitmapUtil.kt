package moe.pyropix.util

import android.graphics.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object BitmapUtil {

    fun resize(bmp: Bitmap, w: Int, h: Int): Bitmap =
        Bitmap.createScaledBitmap(bmp, w, h, true)

    fun crop(bmp: Bitmap, rect: Rect): Bitmap =
        Bitmap.createBitmap(
            bmp,
            rect.left.coerceAtLeast(0),
            rect.top.coerceAtLeast(0),
            rect.width().coerceAtMost(bmp.width - rect.left.coerceAtLeast(0)),
            rect.height().coerceAtMost(bmp.height - rect.top.coerceAtLeast(0))
        )

    fun toBytes(bmp: Bitmap, fmt: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, q: Int = 100): ByteArray {
        val bos = ByteArrayOutputStream()
        bmp.compress(fmt, q, bos)
        return bos.toByteArray()
    }

    fun savePng(bmp: Bitmap, file: File) {
        FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun addWhiteBg(bmp: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bmp, 0f, 0f, null)
        return out
    }
}
