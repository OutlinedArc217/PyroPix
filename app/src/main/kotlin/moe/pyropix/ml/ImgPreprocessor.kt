package moe.pyropix.ml

import android.graphics.Bitmap
import java.nio.FloatBuffer

object ImgPreprocessor {

    private const val IMG_SIZE = 384
    private val MEAN = floatArrayOf(0.5f, 0.5f, 0.5f)
    private val STD = floatArrayOf(0.5f, 0.5f, 0.5f)

    fun preprocess(bmp: Bitmap): FloatBuffer {
        val scaled = Bitmap.createScaledBitmap(bmp, IMG_SIZE, IMG_SIZE, true)
        val pixels = IntArray(IMG_SIZE * IMG_SIZE)
        scaled.getPixels(pixels, 0, IMG_SIZE, 0, 0, IMG_SIZE, IMG_SIZE)
        if (scaled !== bmp) scaled.recycle()

        // CHW layout: [1, 3, H, W]
        val buf = FloatBuffer.allocate(3 * IMG_SIZE * IMG_SIZE)
        val channelSize = IMG_SIZE * IMG_SIZE

        for (c in 0..2) {
            for (i in pixels.indices) {
                val px = pixels[i]
                val raw = when (c) {
                    0 -> (px shr 16) and 0xFF
                    1 -> (px shr 8) and 0xFF
                    else -> px and 0xFF
                }
                buf.put(c * channelSize + i, (raw / 255f - MEAN[c]) / STD[c])
            }
        }
        buf.rewind()
        return buf
    }

    fun imgSize() = IMG_SIZE
}
