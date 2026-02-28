package moe.pyropix.data.repo

import moe.pyropix.data.prefs.AppPrefs
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepo @Inject constructor(private val prefs: AppPrefs) {

    fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> = prefs.get(key, default)

    suspend fun <T> set(key: Preferences.Key<T>, value: T) = prefs.set(key, value)

    fun precision() = prefs.get(AppPrefs.PRECISION, "balanced")
    fun autoCrop() = prefs.get(AppPrefs.AUTO_CROP, true)
    fun outputFmt() = prefs.get(AppPrefs.OUTPUT_FMT, "raw")
    fun fontSize() = prefs.get(AppPrefs.FONT_SIZE, 18f)
    fun cardLayout() = prefs.get(AppPrefs.CARD_LAYOUT, "list")
    fun threadCount() = prefs.get(AppPrefs.THREAD_COUNT, 4)
    fun useGpu() = prefs.get(AppPrefs.USE_GPU, false)
    fun lang() = prefs.get(AppPrefs.LANG, "zh_cn")
    fun penColor() = prefs.get(AppPrefs.PEN_COLOR, 0xFF000000.toInt())
    fun penWidth() = prefs.get(AppPrefs.PEN_WIDTH, 4f)
    fun autoDelay() = prefs.get(AppPrefs.AUTO_DELAY, 800)
    fun palmReject() = prefs.get(AppPrefs.PALM_REJECT, false)
}
