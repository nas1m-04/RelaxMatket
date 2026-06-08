package tj.dastras.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import tj.dastras.data.findActivity
import tj.dastras.data.sendPhoneVerificationCode
import tj.dastras.ui.theme.*

private const val OTP_LENGTH = 6

@Composable
fun OtpScreen(
    phone: String,
    verificationId: String,
    onVerified: () -> Unit,
    onBack: () -> Unit,
) {
    var otp            by remember { mutableStateOf("") }
    var activeVerificationId by remember(verificationId) { mutableStateOf(verificationId) }
    var timer          by remember { mutableStateOf(59) }
    var isError        by remember { mutableStateOf(false) }
    var isVerifying    by remember { mutableStateOf(false) }
    var errorMessage   by remember { mutableStateOf<String?>(null) }

    val activity = LocalContext.current.findActivity()
    val auth     = remember { FirebaseAuth.getInstance() }

    fun verifyCode(code: String) {
        isVerifying = true
        isError     = false
        val credential = PhoneAuthProvider.getCredential(activeVerificationId, code)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            isVerifying = false
            if (task.isSuccessful) {
                onVerified()
            } else {
                isError = true
                otp     = ""
            }
        }
    }

    fun resendCode() {
        val currentActivity = activity ?: return
        timer        = 59
        otp          = ""
        isError      = false
        errorMessage = null
        sendPhoneVerificationCode(
            auth        = auth,
            activity    = currentActivity,
            phoneNumber = phone,
            onCodeSent  = { newVerificationId -> activeVerificationId = newVerificationId },
            onAutoVerified = { credential ->
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) onVerified()
                }
            },
            onError = { message -> errorMessage = message },
        )
    }

    LaunchedEffect(Unit) {
        while (timer > 0) { delay(1000); timer-- }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
            .statusBarsPadding(),
    ) {
        // Back button
        IconButton(
            onClick  = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(RelaxSurfaceAlt)
        ) {
            Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary, modifier = Modifier.size(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Brush.linearGradient(listOf(RelaxDark, RelaxDarkSecondary)), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Sms, null, tint = RelaxWhite, modifier = Modifier.size(32.dp))
            }

            Spacer(Modifier.height(24.dp))

            Text("Код подтверждения", style = MaterialTheme.typography.headlineLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "Мы отправили SMS с кодом на номер\n$phone",
                color = RelaxTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
            )

            Spacer(Modifier.height(40.dp))

            // OTP input
            OtpInput(
                otp       = otp,
                onOtpChange = { value ->
                    if (value.length <= OTP_LENGTH) {
                        otp     = value
                        isError = false
                        if (value.length == OTP_LENGTH) verifyCode(value)
                    }
                },
                isError   = isError,
            )

            if (isError) {
                Spacer(Modifier.height(8.dp))
                Text("Неверный код. Попробуйте ещё раз.", color = RelaxError, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage!!, color = RelaxError, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(32.dp))

            // Verify button
            Button(
                onClick  = {
                    if (otp.length == OTP_LENGTH) verifyCode(otp)
                    else isError = true
                },
                enabled  = otp.length == OTP_LENGTH && !isVerifying,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = RelaxRed,
                    disabledContainerColor = RelaxDivider,
                    contentColor           = RelaxWhite,
                    disabledContentColor   = RelaxTextHint,
                ),
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(color = RelaxWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Подтвердить", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Resend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                if (timer > 0) {
                    Text("Отправить снова через ", color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "0:${timer.toString().padStart(2,'0')}",
                        color = RelaxDark,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    TextButton(onClick = { resendCode() }) {
                        Text("Отправить код снова", color = RelaxRed, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OtpInput(otp: String, onOtpChange: (String) -> Unit, isError: Boolean) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(OTP_LENGTH) { idx ->
            val char   = otp.getOrNull(idx)?.toString() ?: ""
            val isFocused = idx == otp.length

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RelaxWhite)
                    .border(
                        width = 2.dp,
                        color = when {
                            isError   -> RelaxError
                            isFocused -> RelaxDark
                            char.isNotEmpty() -> RelaxDark.copy(alpha = 0.5f)
                            else      -> RelaxDivider
                        },
                        shape = RoundedCornerShape(14.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (char.isNotEmpty()) {
                    Text(char, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = RelaxTextPrimary)
                } else if (isFocused) {
                    val blink = rememberInfiniteTransition(label = "cursor")
                    val alpha by blink.animateFloat(0f, 1f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "cursor")
                    Box(modifier = Modifier.width(2.dp).height(28.dp).alpha(alpha).background(RelaxDark))
                }
            }
        }
    }

    // Hidden text field for keyboard input
    androidx.compose.foundation.text.BasicTextField(
        value         = otp,
        onValueChange = onOtpChange,
        modifier      = Modifier.size(1.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    )
}
