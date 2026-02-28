package moe.pyropix.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.pyropix.data.prefs.AppPrefs
import moe.pyropix.data.repo.SettingsRepo
import moe.pyropix.ml.ModelManager
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    private val settings: SettingsRepo,
    private val modelMgr: ModelManager,
    @ApplicationContext private val ctx: Context
) : ViewModel() {

    val precision = settings.precision()
        .stateIn(viewModelScope, SharingStarted.Lazily, "balanced")
    val autoCrop = settings.autoCrop()
        .stateIn(viewModelScope, SharingStarted.Lazily, true)
    val outputFmt = settings.outputFmt()
        .stateIn(viewModelScope, SharingStarted.Lazily, "raw")
    val fontSize = settings.fontSize()
        .stateIn(viewModelScope, SharingStarted.Lazily, 18f)
    val cardLayout = settings.cardLayout()
        .stateIn(viewModelScope, SharingStarted.Lazily, "list")
    val threadCount = settings.threadCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 4)
    val useGpu = settings.useGpu()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val penColor = settings.penColor()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0xFF000000.toInt())
    val penWidth = settings.penWidth()
        .stateIn(viewModelScope, SharingStarted.Lazily, 4f)
    val autoDelay = settings.autoDelay()
        .stateIn(viewModelScope, SharingStarted.Lazily, 800)
    val palmReject = settings.palmReject()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setPrecision(v: String) = launch { settings.set(AppPrefs.PRECISION, v) }
    fun setAutoCrop(v: Boolean) = launch { settings.set(AppPrefs.AUTO_CROP, v) }
    fun setOutputFmt(v: String) = launch { settings.set(AppPrefs.OUTPUT_FMT, v) }
    fun setFontSize(v: Float) = launch { settings.set(AppPrefs.FONT_SIZE, v) }
    fun setCardLayout(v: String) = launch { settings.set(AppPrefs.CARD_LAYOUT, v) }
    fun setThreadCount(v: Int) = launch { settings.set(AppPrefs.THREAD_COUNT, v) }
    fun setUseGpu(v: Boolean) = launch { settings.set(AppPrefs.USE_GPU, v) }
    fun setPenColor(v: Int) = launch { settings.set(AppPrefs.PEN_COLOR, v) }
    fun setPenWidth(v: Float) = launch { settings.set(AppPrefs.PEN_WIDTH, v) }
    fun setAutoDelay(v: Int) = launch { settings.set(AppPrefs.AUTO_DELAY, v) }
    fun setPalmReject(v: Boolean) = launch { settings.set(AppPrefs.PALM_REJECT, v) }

    fun modelLoaded() = modelMgr.isLoaded()
    fun modelSize(): String {
        val bytes = modelMgr.modelSize()
        return if (bytes < 1024 * 1024) "${bytes / 1024} KB" else "${"%.1f".format(bytes / 1048576.0)} MB"
    }

    fun backup() {
        viewModelScope.launch {
            val src = ctx.getDatabasePath("pyro.db")
            val dst = File(ctx.getExternalFilesDir(null), "pyro_backup.db")
            if (src.exists()) src.copyTo(dst, overwrite = true)
        }
    }

    fun restore() {
        viewModelScope.launch {
            val src = File(ctx.getExternalFilesDir(null), "pyro_backup.db")
            val dst = ctx.getDatabasePath("pyro.db")
            if (src.exists()) src.copyTo(dst, overwrite = true)
        }
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
