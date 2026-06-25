package tj.dastras.core.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    val bits  = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bmp   = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size)
        for (y in 0 until size)
            bmp.setPixel(x, y, if (bits[x, y]) 0xFF0F172A.toInt() else 0xFFFFFFFF.toInt())
    return bmp
}