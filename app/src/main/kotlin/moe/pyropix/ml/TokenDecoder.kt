package moe.pyropix.ml

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.*
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDecoder @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    private var vocab: Map<Int, String> = emptyMap()
    private var bosId = 1
    private var eosId = 2
    private var padId = 0
    private var decStartId = 2

    fun load() {
        val file = File(ctx.filesDir, "models/tokenizer.json")
        if (!file.exists()) return
        val json = Json.parseToJsonElement(file.readText()).jsonObject
        val model = json["model"]?.jsonObject
        val vocabObj = model?.get("vocab")?.jsonObject ?: return
        vocab = vocabObj.entries.associate { (k, v) -> v.jsonPrimitive.int to k }

        json["added_tokens"]?.jsonArray?.forEach { tok ->
            val obj = tok.jsonObject
            val id = obj["id"]?.jsonPrimitive?.int ?: return@forEach
            val content = obj["content"]?.jsonPrimitive?.content ?: return@forEach
            when (content) {
                "<s>" -> bosId = id
                "</s>" -> eosId = id
                "<pad>" -> padId = id
            }
        }

        // TrOCR uses eos_token_id as decoder_start_token_id
        val genFile = File(ctx.filesDir, "models/generation_config.json")
        if (genFile.exists()) {
            val genJson = Json.parseToJsonElement(genFile.readText()).jsonObject
            decStartId = genJson["decoder_start_token_id"]?.jsonPrimitive?.int ?: eosId
        }
    }

    fun decode(ids: List<Int>): String {
        val sb = StringBuilder()
        for (id in ids) {
            if (id == eosId || id == padId) break
            if (id == bosId || id == decStartId) continue
            val token = vocab[id] ?: continue
            sb.append(token.replace("▁", " "))
        }
        return sb.toString().trim()
    }

    fun decoderStartId() = decStartId
    fun bosId() = bosId
    fun eosId() = eosId
    fun vocabSize() = vocab.size
}
