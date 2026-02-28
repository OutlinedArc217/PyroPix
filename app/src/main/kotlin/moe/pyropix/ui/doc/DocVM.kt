package moe.pyropix.ui.doc

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.pyropix.doc.DocManager
import moe.pyropix.doc.DocType
import javax.inject.Inject

data class DocItem(
    val uri: String,
    val name: String,
    val type: DocType,
    val size: Long
)

@HiltViewModel
class DocVM @Inject constructor(
    private val docMgr: DocManager
) : ViewModel() {

    private val _docList = MutableStateFlow<List<DocItem>>(emptyList())
    val docList = _docList.asStateFlow()

    private val _currentText = MutableStateFlow("")
    val currentText = _currentText.asStateFlow()

    private val _extractedFormulas = MutableStateFlow<List<String>>(emptyList())
    val extractedFormulas = _extractedFormulas.asStateFlow()

    fun addDoc(item: DocItem) {
        _docList.value = listOf(item) + _docList.value.filter { it.uri != item.uri }
    }

    fun loadDoc(ctx: Context, uri: Uri) {
        viewModelScope.launch {
            val type = docMgr.detect(uri, ctx.contentResolver.getType(uri))
            _currentText.value = docMgr.readText(ctx, uri, type)
        }
    }

    fun extractFormulas(ctx: Context, uri: Uri) {
        viewModelScope.launch {
            val type = docMgr.detect(uri, ctx.contentResolver.getType(uri))
            _extractedFormulas.value = docMgr.extractFormulas(ctx, uri, type)
        }
    }

    fun saveText(ctx: Context, uri: Uri, text: String) {
        viewModelScope.launch {
            try {
                ctx.contentResolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray()) }
                _currentText.value = text
            } catch (_: Exception) { }
        }
    }

    fun updateText(text: String) {
        _currentText.value = text
    }
}
