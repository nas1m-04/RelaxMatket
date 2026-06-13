package tj.dastras.data

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TAJIKISTAN_NATIONAL_NUMBER_LENGTH = 9

/**
 * Normalizes user input into an E.164 number for Tajikistan (+992 followed by a 9-digit
 * national number), accepting "+992...", "992...", a leading trunk "0", or a bare local number.
 */
fun toE164PhoneNumber(raw: String): String {
    val digits = raw.trim().replace("+", "").filter { it.isDigit() }

    val national = when {
        digits.startsWith("992") -> digits.removePrefix("992")
        digits.startsWith("0") -> digits.drop(1)
        else -> digits
    }

    return "+992$national"
}

/** True if [raw] normalizes to a complete 9-digit Tajik national number. */
fun isValidTajikPhoneNumber(raw: String): Boolean =
    toE164PhoneNumber(raw).removePrefix("+992").length == TAJIKISTAN_NATIONAL_NUMBER_LENGTH
