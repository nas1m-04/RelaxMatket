package tj.relax.core.util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/** Portable replacement for JVM-only `"%.${decimals}f".format(this)` — kotlin.text.format is not
 * available on Kotlin/Native. */
fun Double.toFixed(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = (this * factor).roundToLong()
    val sign = if (rounded < 0) "-" else ""
    val absRounded = abs(rounded)
    val divisor = factor.toLong()
    val intPart = absRounded / divisor
    if (decimals == 0) return "$sign$intPart"
    val fracPart = (absRounded % divisor).toString().padStart(decimals, '0')
    return "$sign$intPart.$fracPart"
}
