package moe.pyropix.util

object LatexUtil {

    fun clean(raw: String): String = raw
        .trim()
        .removePrefix("\\(").removeSuffix("\\)")
        .removePrefix("\\[").removeSuffix("\\]")
        .removePrefix("$").removeSuffix("$")
        .trim()

    fun wrap(latex: String, fmt: String): String = when (fmt) {
        "inline" -> "\$$latex\$"
        "display" -> "\$\$$latex\$\$"
        "equation" -> "\\begin{equation}\n$latex\n\\end{equation}"
        else -> latex
    }

    fun extractInline(text: String): List<String> {
        val results = mutableListOf<String>()
        val pattern = Regex("""\$\$(.+?)\$\$|\$(.+?)\$""", RegexOption.DOT_MATCHES_ALL)
        pattern.findAll(text).forEach { m ->
            val latex = m.groupValues[1].ifEmpty { m.groupValues[2] }
            if (latex.isNotBlank()) results.add(latex.trim())
        }
        return results
    }

    fun isEquation(latex: String): Boolean {
        val l = latex.trim()
        return l.contains("=") && !l.startsWith("\\begin")
    }

    fun isFunction(latex: String): Boolean {
        val l = latex.lowercase()
        return (l.contains("x") || l.contains("t")) &&
                !l.contains("=") &&
                (l.contains("^") || l.contains("\\frac") || l.contains("\\sin") || l.contains("\\cos"))
    }
}
