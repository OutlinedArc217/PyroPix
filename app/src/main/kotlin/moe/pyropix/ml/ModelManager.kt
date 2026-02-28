package moe.pyropix.ml

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import moe.pyropix.util.FileUtil
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelManager @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val tokenDecoder: TokenDecoder
) {
    private val env = OrtEnvironment.getEnvironment()
    private var encoderSession: OrtSession? = null
    private var decoderSession: OrtSession? = null
    private val mutex = Mutex()
    private var loaded = false

    val modelDir: File get() = File(ctx.filesDir, "models")

    suspend fun ensureLoaded(threadCount: Int = 4, useGpu: Boolean = false) = mutex.withLock {
        if (loaded) return@withLock
        withContext(Dispatchers.IO) { copyModels() }
        val opts = OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(threadCount)
            if (useGpu) {
                try { addNnapi() } catch (_: Exception) { }
            }
        }
        val encFile = File(modelDir, "encoder_model.onnx")
        val decFile = File(modelDir, "decoder_model.onnx")
        if (encFile.exists()) encoderSession = env.createSession(encFile.absolutePath, opts)
        if (decFile.exists()) decoderSession = env.createSession(decFile.absolutePath, opts)
        tokenDecoder.load()
        loaded = true
    }

    private fun copyModels() {
        val assets = try { ctx.assets.list("models") ?: emptyArray() } catch (_: Exception) { emptyArray() }
        for (name in assets) {
            FileUtil.copyAsset(ctx, "models/$name", File(modelDir, name))
        }
    }

    fun runEncoder(input: OnnxTensor): OrtSession.Result? = encoderSession?.run(mapOf("pixel_values" to input))

    fun runDecoder(inputs: Map<String, OnnxTensor>): OrtSession.Result? = decoderSession?.run(inputs)

    fun close() {
        encoderSession?.close()
        decoderSession?.close()
        encoderSession = null
        decoderSession = null
        loaded = false
    }

    fun isLoaded() = loaded

    fun modelSize(): Long = modelDir.listFiles()?.sumOf { it.length() } ?: 0L
}
