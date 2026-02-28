package moe.pyropix.ui.widget

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexView(
    latex: String,
    modifier: Modifier = Modifier,
    fontSize: Float = 18f
) {
    val dark = isSystemInDarkTheme()
    val bg = if (dark) "#1C1B1F" else "#FFFBFE"
    val fg = if (dark) "#E6E1E5" else "#1C1B1F"

    val html = remember(latex, fontSize, dark) {
        """
        <!DOCTYPE html>
        <html><head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
        <script src="file:///android_asset/katex/katex.min.js"></script>
        <style>
          body { margin:8px; background:$bg; color:$fg; font-size:${fontSize}px; }
          #formula { text-align:center; overflow-x:auto; }
        </style>
        </head><body>
        <div id="formula"></div>
        <script>
          try {
            katex.render(${escapeJs(latex)}, document.getElementById('formula'), {
              displayMode: true, throwOnError: false, trust: true
            });
          } catch(e) {
            document.getElementById('formula').textContent = ${escapeJs(latex)};
          }
        </script>
        </body></html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                webViewClient = WebViewClient()
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { wv ->
            wv.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "utf-8",
                null
            )
        }
    )
}

private fun escapeJs(s: String): String {
    val escaped = s.replace("\\", "\\\\")
        .replace("'", "\\'")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
    return "\"$escaped\""
}
