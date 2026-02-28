package moe.pyropix.util

import android.content.Context
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class I18n @Inject constructor() {

    private var strings: Map<String, String> = emptyMap()
    private var currentLang = "zh_cn"

    fun load(ctx: Context, lang: String = "zh_cn") {
        currentLang = lang
        val json = try {
            ctx.assets.open("i18n/$lang.json").bufferedReader().use { it.readText() }
        } catch (_: Exception) { return }
        val obj = Json.parseToJsonElement(json).jsonObject
        strings = flattenJson(obj)
    }

    fun t(key: String): String = strings[key] ?: key

    fun lang() = currentLang

    private fun flattenJson(obj: JsonObject, prefix: String = ""): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for ((k, v) in obj) {
            val fullKey = if (prefix.isEmpty()) k else "$prefix.$k"
            when (v) {
                is JsonPrimitive -> map[fullKey] = v.content
                is JsonObject -> map.putAll(flattenJson(v, fullKey))
                else -> {}
            }
        }
        return map
    }
}
