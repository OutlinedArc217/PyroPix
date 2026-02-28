package moe.pyropix.ui.mine

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import moe.pyropix.data.db.FormulaEntity
import moe.pyropix.data.db.GroupEntity
import moe.pyropix.data.repo.FormulaRepo
import moe.pyropix.data.repo.SettingsRepo
import javax.inject.Inject

@Serializable
data class TemplateFormula(val name: String, val latex: String)

@Serializable
data class TemplateCategory(val id: String, val name: String, val formulas: List<TemplateFormula>)

@Serializable
private data class TemplateRoot(val categories: List<TemplateCategory>)

@HiltViewModel
class MineVM @Inject constructor(
    private val repo: FormulaRepo,
    private val settings: SettingsRepo,
    @ApplicationContext private val ctx: Context
) : ViewModel() {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    val history: StateFlow<List<FormulaEntity>> = repo.allFormulas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favorites: StateFlow<List<FormulaEntity>> = repo.favFormulas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val groups: StateFlow<List<GroupEntity>> = repo.allGroups()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _templates = MutableStateFlow<List<TemplateCategory>>(emptyList())
    val templates = _templates.asStateFlow()

    init {
        loadTemplates()
    }

    fun toggleFav(formula: FormulaEntity) {
        viewModelScope.launch {
            repo.update(formula.copy(isFav = !formula.isFav))
        }
    }

    fun deleteFormulas(ids: List<Long>) {
        viewModelScope.launch { repo.deleteByIds(ids) }
    }

    fun addGroup(name: String) {
        viewModelScope.launch {
            repo.addGroup(GroupEntity(name = name, sortOrder = groups.value.size))
        }
    }

    fun loadTemplates() {
        viewModelScope.launch {
            val json = try {
                ctx.assets.open("templates.json").bufferedReader().use { it.readText() }
            } catch (_: Exception) { return@launch }
            _templates.value = try {
                jsonParser.decodeFromString<TemplateRoot>(json).categories
            } catch (_: Exception) { emptyList() }
        }
    }

    fun byGroup(gid: Long): Flow<List<FormulaEntity>> = repo.byGroup(gid)

    fun search(q: String): Flow<List<FormulaEntity>> = repo.search(q)

    fun deleteFormula(f: FormulaEntity) {
        viewModelScope.launch { repo.delete(f) }
    }

    fun saveFormula(latex: String, source: String = "template") {
        viewModelScope.launch {
            repo.save(FormulaEntity(latex = latex, source = source))
        }
    }
}
