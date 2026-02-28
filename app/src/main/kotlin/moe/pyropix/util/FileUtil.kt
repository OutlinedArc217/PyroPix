package moe.pyropix.util

import android.content.Context
import android.net.Uri
import java.io.*

object FileUtil {

    fun copyAsset(ctx: Context, assetPath: String, dest: File) {
        if (dest.exists()) return
        dest.parentFile?.mkdirs()
        ctx.assets.open(assetPath).use { input ->
            FileOutputStream(dest).use { output -> input.copyTo(output) }
        }
    }

    fun readText(ctx: Context, uri: Uri): String? =
        ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }

    fun writeText(ctx: Context, uri: Uri, text: String) {
        ctx.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use { it.write(text) }
    }

    fun tempFile(ctx: Context, prefix: String, ext: String): File =
        File.createTempFile(prefix, ext, ctx.cacheDir)

    fun exportDir(ctx: Context): File {
        val dir = File(ctx.getExternalFilesDir(null), "export")
        dir.mkdirs()
        return dir
    }

    fun backupDir(ctx: Context): File {
        val dir = File(ctx.getExternalFilesDir(null), "backup")
        dir.mkdirs()
        return dir
    }
}
