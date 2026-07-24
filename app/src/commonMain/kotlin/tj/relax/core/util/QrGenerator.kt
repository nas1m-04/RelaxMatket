package tj.relax.core.util

import androidx.compose.ui.graphics.ImageBitmap

/** Renders [content] as a square QR code image. ZXing (the Android implementation's encoder)
 * is a JVM-only library, so the whole thing — not just rasterization — is expect/actual. */
expect fun generateQrImageBitmap(content: String, size: Int = 512): ImageBitmap
