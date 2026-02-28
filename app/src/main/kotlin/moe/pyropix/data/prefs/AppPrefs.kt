package moe.pyropix.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ds by preferencesDataStore("pyropix_prefs")

@Singleton
class AppPrefs @Inject constructor(private val ctx: Context) {

    companion object {
        val PRECISION = stringPreferencesKey("precision")
        val AUTO_CROP = booleanPreferencesKey("auto_crop")
        val OUTPUT_FMT = stringPreferencesKey("output_fmt")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val CARD_LAYOUT = stringPreferencesKey("card_layout")
        val EXPORT_PATH = stringPreferencesKey("export_path")
        val PEN_COLOR = intPreferencesKey("pen_color")
        val PEN_WIDTH = floatPreferencesKey("pen_width")
        val AUTO_DELAY = intPreferencesKey("auto_delay")
        val PALM_REJECT = booleanPreferencesKey("palm_reject")
        val THREAD_COUNT = intPreferencesKey("thread_count")
        val USE_GPU = booleanPreferencesKey("use_gpu")
        val LANG = stringPreferencesKey("lang")
    }

    val data: Flow<Preferences> = ctx.ds.data

    fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> =
        ctx.ds.data.map { it[key] ?: default }

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        ctx.ds.edit { it[key] = value }
    }
}
