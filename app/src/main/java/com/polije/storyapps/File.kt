package com.polije.storyapps

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun getFile(context: Context, uri: Uri?): File? {
        if (uri == null) return null
        val contentResolver = context.contentResolver
        val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val tempFile = File(context.cacheDir, fileName)
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return tempFile
    }
}
