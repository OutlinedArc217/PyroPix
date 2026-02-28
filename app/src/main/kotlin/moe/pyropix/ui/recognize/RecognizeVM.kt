package moe.pyropix.ui.recognize

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.pyropix.data.db.FormulaEntity
import moe.pyropix.data.repo.FormulaRepo
import moe.pyropix.data.repo.SettingsRepo
import moe.pyropix.ml.FormulaRecognizer
import moe.pyropix.ml.ModelManager
import javax.inject.Inject

@HiltViewModel
class RecognizeVM @Inject constructor(
    private val recognizer: FormulaRecognizer,
    private val formulaRepo: FormulaRepo,
    private val modelMgr: ModelManager,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    private val _latex = MutableStateFlow("")
    val recognizedLatex = _latex.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val isLoading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _batchResults = MutableStateFlow<List<String>>(emptyList())
    val batchResults = _batchResults.asStateFlow()

    fun recognize(bitmap: Bitmap) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                modelMgr.ensureLoaded()
                _latex.value = recognizer.recognize(bitmap)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun batchRecognize(bitmaps: List<Bitmap>) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _batchResults.value = emptyList()
            try {
                modelMgr.ensureLoaded()
                val results = mutableListOf<String>()
                for (bmp in bitmaps) {
                    results.add(recognizer.recognize(bmp))
                }
                _batchResults.value = results
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveFormula(latex: String, source: String) {
        viewModelScope.launch {
            formulaRepo.save(FormulaEntity(latex = latex, source = source))
        }
    }

    fun clearResult() {
        _latex.value = ""
        _error.value = null
        _batchResults.value = emptyList()
    }
}
