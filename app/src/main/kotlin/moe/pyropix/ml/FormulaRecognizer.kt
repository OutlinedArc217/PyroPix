package moe.pyropix.ml

import android.graphics.Bitmap
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormulaRecognizer @Inject constructor(
    private val modelMgr: ModelManager,
    private val tokenDecoder: TokenDecoder
) {
    private val env = OrtEnvironment.getEnvironment()
    private val maxLen = 512

    suspend fun recognize(bmp: Bitmap, beamWidth: Int = 1): String = withContext(Dispatchers.Default) {
        val buf = ImgPreprocessor.preprocess(bmp)
        val sz = ImgPreprocessor.imgSize().toLong()
        val inputTensor = OnnxTensor.createTensor(env, buf, longArrayOf(1, 3, sz, sz))

        val encResult = modelMgr.runEncoder(inputTensor)
            ?: return@withContext ""
        val encHidden = encResult.first().value as Array<*>

        val tokenIds = if (beamWidth <= 1) greedyDecode(encHidden) else beamDecode(encHidden, beamWidth)

        inputTensor.close()
        encResult.close()

        tokenDecoder.decode(tokenIds)
    }

    private fun greedyDecode(encHidden: Array<*>): List<Int> {
        val ids = mutableListOf(tokenDecoder.decoderStartId())
        for (step in 0 until maxLen) {
            val decIds = ids.toLongArray()
            val decTensor = OnnxTensor.createTensor(env, arrayOf(decIds))
            val encTensor = OnnxTensor.createTensor(env, encHidden)

            val decResult = modelMgr.runDecoder(
                mapOf("input_ids" to decTensor, "encoder_hidden_states" to encTensor)
            )
            if (decResult == null) { decTensor.close(); encTensor.close(); break }

            val logits = decResult.first().value
            val nextId = argmax(logits)
            decTensor.close()
            encTensor.close()
            decResult.close()

            if (nextId == tokenDecoder.eosId()) break
            ids.add(nextId)
        }
        return ids
    }

    private fun beamDecode(encHidden: Array<*>, beamWidth: Int): List<Int> {
        data class Beam(val ids: List<Int>, val score: Double)

        var beams = listOf(Beam(listOf(tokenDecoder.decoderStartId()), 0.0))

        for (step in 0 until maxLen) {
            val candidates = mutableListOf<Beam>()
            for (beam in beams) {
                val decIds = beam.ids.map { it.toLong() }.toLongArray()
                val decTensor = OnnxTensor.createTensor(env, arrayOf(decIds))
                val encTensor = OnnxTensor.createTensor(env, encHidden)

                val decResult = modelMgr.runDecoder(
                    mapOf("input_ids" to decTensor, "encoder_hidden_states" to encTensor)
                ) ?: continue

                val logits = decResult.first().value
                val topK = topKIndices(logits, beamWidth)
                val probs = softmax(logits)

                for (idx in topK) {
                    val newScore = beam.score + Math.log(probs[idx].coerceAtLeast(1e-10).toDouble())
                    candidates.add(Beam(beam.ids + idx, newScore))
                }
                decTensor.close()
                encTensor.close()
                decResult.close()
            }

            beams = candidates.sortedByDescending { it.score }.take(beamWidth)
            if (beams.all { it.ids.last() == tokenDecoder.eosId() }) break
        }
        return beams.maxByOrNull { it.score }?.ids ?: emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun argmax(logits: Any): Int {
        val arr = extractLastLogits(logits)
        var maxIdx = 0
        var maxVal = Float.NEGATIVE_INFINITY
        for (i in arr.indices) {
            if (arr[i] > maxVal) { maxVal = arr[i]; maxIdx = i }
        }
        return maxIdx
    }

    @Suppress("UNCHECKED_CAST")
    private fun topKIndices(logits: Any, k: Int): List<Int> {
        val arr = extractLastLogits(logits)
        return arr.indices.sortedByDescending { arr[it] }.take(k)
    }

    @Suppress("UNCHECKED_CAST")
    private fun softmax(logits: Any): FloatArray {
        val arr = extractLastLogits(logits)
        val max = arr.max()
        val exps = FloatArray(arr.size) { Math.exp((arr[it] - max).toDouble()).toFloat() }
        val sum = exps.sum()
        return FloatArray(exps.size) { exps[it] / sum }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractLastLogits(logits: Any): FloatArray {
        return when (logits) {
            is Array<*> -> {
                val batch = logits as Array<Array<FloatArray>>
                batch[0].last()
            }
            else -> floatArrayOf()
        }
    }
}
