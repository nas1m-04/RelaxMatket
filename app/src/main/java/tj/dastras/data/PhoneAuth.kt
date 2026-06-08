package tj.dastras.data

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TAJIKISTAN_COUNTRY_CODE = "992"
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

/** Requests an SMS verification code for [phoneNumber] via Firebase Phone Auth. */
fun sendPhoneVerificationCode(
    auth: FirebaseAuth,
    activity: Activity,
    phoneNumber: String,
    onCodeSent: (verificationId: String) -> Unit,
    onAutoVerified: (PhoneAuthCredential) -> Unit,
    onError: (message: String) -> Unit,
) {
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                onAutoVerified(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                onError(
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> "Неверный формат номера телефона"
                        is FirebaseTooManyRequestsException -> "Превышен лимит запросов. Попробуйте позже"
                        else -> e.localizedMessage ?: "Не удалось отправить код. Попробуйте снова"
                    }
                )
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }
        })
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}
