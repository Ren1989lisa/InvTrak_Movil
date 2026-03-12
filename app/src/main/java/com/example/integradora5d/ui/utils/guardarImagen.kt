package com.example.integradora5d.ui.utils


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.OutputStream

fun guardarImagen(context: Context, bitmap: Bitmap, nombre: String) {

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$nombre.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/InvTrack")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )

    uri?.let {
        val outputStream: OutputStream? =
            context.contentResolver.openOutputStream(it)

        outputStream?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }
    }
}