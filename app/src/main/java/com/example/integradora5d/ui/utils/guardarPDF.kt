package com.example.integradora5d.ui.utils


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.provider.MediaStore
import java.io.OutputStream

fun guardarPDF(context: Context, bitmap: Bitmap, nombre: String) {

    val pdfDocument = PdfDocument()

    val pageInfo = PdfDocument.PageInfo.Builder(
        bitmap.width,
        bitmap.height,
        1
    ).create()

    val page = pdfDocument.startPage(pageInfo)

    page.canvas.drawBitmap(bitmap, 0f, 0f, null)

    pdfDocument.finishPage(page)

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$nombre.pdf")
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/InvTrack")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Files.getContentUri("external"),
        values
    )

    uri?.let {

        val outputStream: OutputStream? =
            context.contentResolver.openOutputStream(it)

        pdfDocument.writeTo(outputStream)

        outputStream?.close()
    }

    pdfDocument.close()
}