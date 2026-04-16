package com.example.integradora5d.ui.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generarQR(texto: String): Bitmap {
    val writer = QRCodeWriter()
    // 400x400 es un buen tamaño estándar
    val bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 400, 400)

    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(
                x, y,
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            )
        }
    }

    return bitmap
}