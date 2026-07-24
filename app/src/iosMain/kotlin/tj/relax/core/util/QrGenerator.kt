package tj.relax.core.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import qrcode.QRCode

actual fun generateQrImageBitmap(content: String, size: Int): ImageBitmap {
    val pngBytes = QRCode.ofSquares()
        .withCanvasSize(size)
        .build(content)
        .renderToBytes(format = "PNG")
    return Image.makeFromEncoded(pngBytes).toComposeImageBitmap()
}
