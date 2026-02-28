package moe.pyropix.solver

object LatexTranslator {

    fun toSymja(latex: String): String {
        var s = latex.trim()
        s = s.replace("\\frac{", "((")
            .replace("}{", ")/(")
        s = s.replace("\\sqrt{", "Sqrt(")
        s = s.replace("\\sin", "Sin")
            .replace("\\cos", "Cos")
            .replace("\\tan", "Tan")
            .replace("\\ln", "Log")
            .replace("\\log", "Log10")
            .replace("\\exp", "Exp")
        s = s.replace("\\pi", "Pi")
            .replace("\\infty", "Infinity")
            .replace("\\alpha", "alpha")
            .replace("\\beta", "beta")
            .replace("\\theta", "theta")

        // integrals: \int ... dx -> Integrate[..., x]
        val intRegex = Regex("""\\int\s*(.+?)\s*d([a-z])""")
        s = intRegex.replace(s) { m ->
            "Integrate(${m.groupValues[1]}, ${m.groupValues[2]})"
        }

        // derivatives: \frac{d}{dx} expr -> D(expr, x)
        val derivRegex = Regex("""\\frac\{d\}\{d([a-z])\}\s*(.+)""")
        s = derivRegex.replace(s) { m ->
            "D(${m.groupValues[2]}, ${m.groupValues[1]})"
        }

        // limits: \lim_{x \to a} expr -> Limit(expr, x->a)
        val limRegex = Regex("""\\lim_\{([a-z])\s*\\to\s*(.+?)\}\s*(.+)""")
        s = limRegex.replace(s) { m ->
            "Limit(${m.groupValues[3]}, ${m.groupValues[1]}->${m.groupValues[2]})"
        }

        // sum: \sum_{n=a}^{b} expr -> Sum(expr, {n, a, b})
        val sumRegex = Regex("""\\sum_\{([a-z])=(.+?)\}\^\{(.+?)\}\s*(.+)""")
        s = sumRegex.replace(s) { m ->
            "Sum(${m.groupValues[4]}, {${m.groupValues[1]}, ${m.groupValues[2]}, ${m.groupValues[3]}})"
        }

        s = s.replace("^{", "^(")
        s = s.replace("_{", "_(")
        s = s.replace("{", "(").replace("}", ")")
        s = s.replace("\\left", "").replace("\\right", "")
        s = s.replace("\\cdot", "*").replace("\\times", "*")
        return s.trim()
    }

    fun toLatex(symja: String): String {
        var s = symja
        s = s.replace("Sqrt(", "\\sqrt{")
        s = s.replace("Sin(", "\\sin(")
            .replace("Cos(", "\\cos(")
            .replace("Tan(", "\\tan(")
            .replace("Log(", "\\ln(")
            .replace("Log10(", "\\log(")
        s = s.replace("Pi", "\\pi")
            .replace("Infinity", "\\infty")
            .replace("I", "i")

        // fractions: a/b -> \frac{a}{b}
        val fracRegex = Regex("""\((.+?)\)/\((.+?)\)""")
        s = fracRegex.replace(s) { m ->
            "\\frac{${m.groupValues[1]}}{${m.groupValues[2]}}"
        }

        s = s.replace("^(", "^{")
        // close matching braces
        s = balanceBraces(s)
        return s
    }

    private fun balanceBraces(s: String): String {
        val sb = StringBuilder()
        var depth = 0
        for (c in s) {
            when (c) {
                '(' -> { sb.append('{'); depth++ }
                ')' -> { if (depth > 0) { sb.append('}'); depth-- } else sb.append(')') }
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }
}
