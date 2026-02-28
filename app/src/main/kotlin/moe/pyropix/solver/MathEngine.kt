package moe.pyropix.solver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.matheclipse.core.eval.ExprEvaluator
import org.matheclipse.core.expression.F
import org.matheclipse.core.interfaces.IExpr
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MathEngine @Inject constructor() {

    private val eval = ExprEvaluator(false, 100)

    init {
        F.initSymbols()
    }

    suspend fun simplify(latex: String): SolveResult = solve("Simplify(${LatexTranslator.toSymja(latex)})")
    suspend fun expand(latex: String): SolveResult = solve("Expand(${LatexTranslator.toSymja(latex)})")
    suspend fun factor(latex: String): SolveResult = solve("Factor(${LatexTranslator.toSymja(latex)})")

    suspend fun solveEq(latex: String, variable: String = "x"): SolveResult {
        val expr = LatexTranslator.toSymja(latex)
        val parts = expr.split("=")
        val cmd = if (parts.size == 2) {
            "Solve(${parts[0].trim()}==${parts[1].trim()}, $variable)"
        } else {
            "Solve($expr==0, $variable)"
        }
        return solve(cmd)
    }

    suspend fun differentiate(latex: String, variable: String = "x"): SolveResult =
        solve("D(${LatexTranslator.toSymja(latex)}, $variable)")

    suspend fun integrate(latex: String, variable: String = "x"): SolveResult =
        solve("Integrate(${LatexTranslator.toSymja(latex)}, $variable)")

    suspend fun limit(latex: String): SolveResult {
        val symja = LatexTranslator.toSymja(latex)
        return solve(symja)
    }

    suspend fun evaluate(cmd: String): SolveResult = solve(cmd)

    private suspend fun solve(cmd: String): SolveResult = withContext(Dispatchers.Default) {
        try {
            val result = eval.eval(cmd)
            val steps = collectSteps(result)
            SolveResult(
                input = cmd,
                latex = LatexTranslator.toLatex(result.toString()),
                raw = result.toString(),
                steps = steps,
                success = true
            )
        } catch (e: Exception) {
            SolveResult(input = cmd, latex = "", raw = e.message ?: "error", steps = emptyList(), success = false)
        }
    }

    private fun collectSteps(expr: IExpr): List<SolveStep> {
        val steps = mutableListOf<SolveStep>()
        steps.add(SolveStep("结果", LatexTranslator.toLatex(expr.toString())))
        return steps
    }

    fun evalPoints(expr: String, variable: String, from: Double, to: Double, n: Int = 200): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        val step = (to - from) / n
        for (i in 0..n) {
            val x = from + i * step
            try {
                val cmd = expr.replace(variable, "($x)")
                val y = eval.eval(cmd).evalDouble()
                if (y.isFinite()) points.add(x to y)
            } catch (_: Exception) { }
        }
        return points
    }
}

data class SolveResult(
    val input: String,
    val latex: String,
    val raw: String,
    val steps: List<SolveStep>,
    val success: Boolean
)

data class SolveStep(val label: String, val latex: String)
