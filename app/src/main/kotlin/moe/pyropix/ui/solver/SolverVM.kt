package moe.pyropix.ui.solver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.pyropix.data.db.FormulaEntity
import moe.pyropix.data.repo.FormulaRepo
import moe.pyropix.solver.*
import javax.inject.Inject

@HiltViewModel
class SolverVM @Inject constructor(
    private val engine: MathEngine,
    private val repo: FormulaRepo
) : ViewModel() {

    private val _inputLatex = MutableStateFlow("")
    val inputLatex = _inputLatex.asStateFlow()

    private val _result = MutableStateFlow<SolveResult?>(null)
    val result = _result.asStateFlow()

    private val _steps = MutableStateFlow<List<SolveStep>>(emptyList())
    val steps = _steps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _graphPoints = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val graphPoints = _graphPoints.asStateFlow()

    fun setInput(latex: String) {
        _inputLatex.value = latex
    }

    fun solve(type: SolveType) {
        val latex = _inputLatex.value.ifBlank { return }
        _isLoading.value = true
        viewModelScope.launch {
            val res = when (type) {
                SolveType.SIMPLIFY -> engine.simplify(latex)
                SolveType.EXPAND -> engine.expand(latex)
                SolveType.FACTOR -> engine.factor(latex)
                SolveType.EQUATION -> engine.solveEq(latex)
                SolveType.DIFFERENTIATE -> engine.differentiate(latex)
                SolveType.INTEGRATE -> engine.integrate(latex)
                SolveType.LIMIT -> engine.limit(latex)
                SolveType.SUM -> engine.evaluate(LatexTranslator.toSymja(latex))
                SolveType.MATRIX -> engine.evaluate(LatexTranslator.toSymja(latex))
            }
            _result.value = res
            _steps.value = StepFormatter.format(res)
            _isLoading.value = false
            if (res.success) {
                repo.save(FormulaEntity(latex = latex, source = "solver"))
            }
        }
    }

    fun plotGraph(expr: String, variable: String = "x") {
        viewModelScope.launch {
            val symja = LatexTranslator.toSymja(expr)
            _graphPoints.value = engine.evalPoints(symja, variable, -10.0, 10.0)
        }
    }
}
