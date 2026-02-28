package moe.pyropix.solver

object StepFormatter {

    fun format(result: SolveResult): List<SolveStep> {
        if (!result.success) return listOf(SolveStep("错误", result.raw))
        val steps = mutableListOf<SolveStep>()
        steps.add(SolveStep("输入", result.input))
        steps.addAll(result.steps)
        if (result.latex.isNotBlank()) {
            steps.add(SolveStep("最终结果", result.latex))
        }
        return steps
    }

    fun detectType(latex: String): SolveType {
        val l = latex.trim()
        return when {
            l.contains("\\int") -> SolveType.INTEGRATE
            l.contains("\\frac{d}") -> SolveType.DIFFERENTIATE
            l.contains("\\lim") -> SolveType.LIMIT
            l.contains("\\sum") -> SolveType.SUM
            l.contains("=") -> SolveType.EQUATION
            l.contains("^") || l.contains("\\frac") -> SolveType.SIMPLIFY
            else -> SolveType.SIMPLIFY
        }
    }
}

enum class SolveType {
    SIMPLIFY, EXPAND, FACTOR, EQUATION, DIFFERENTIATE, INTEGRATE, LIMIT, SUM, MATRIX
}
