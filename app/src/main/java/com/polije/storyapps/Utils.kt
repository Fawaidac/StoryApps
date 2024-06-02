package com.polije.storyapps

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            imageFile
        )
    }
    fun appSettingOpen(context: Context){
    Toast.makeText(
        context,
        "Go to Setting and Enable All Permission",
        Toast.LENGTH_LONG
    ).show()

    val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    settingIntent.data = Uri.parse("package:${context.packageName}")
    context.startActivity(settingIntent)
}

    fun warningPermissionDialog(context: Context,listener : DialogInterface.OnClickListener){
    MaterialAlertDialogBuilder(context)
        .setMessage("All Permission are Required for this app")
        .setCancelable(false)
        .setPositiveButton("Ok",listener)
        .create()
        .show()
}