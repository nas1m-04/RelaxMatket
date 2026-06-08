package tj.dastras.ui.screens.auth

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import com.google.firebase.auth.FirebaseAuth
import tj.dastras.data.findActivity
import tj.dastras.data.sendPhoneVerificationCode
import tj.dastras.data.toE164PhoneNumber
import tj.dastras.ui.components.RelaxButton
import tj.dastras.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToOtp: (phone: String, verificationId: String) -> Unit,
    onNavigateToMain: () -> Unit,
) {
    var phone    by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val activity = LocalContext.current.findActivity()
    val auth     = remember { FirebaseAuth.getInstance() }

    fun requestVerificationCode() {
        val currentActivity = activity ?: return
        if (phone.isBlank() || isLoading) return

        errorMessage = null
        isLoading    = true
        val fullPhone = toE164PhoneNumber(phone)

        sendPhoneVerificationCode(
            auth        = auth,
            activity    = currentActivity,
            phoneNumber = fullPhone,
            onCodeSent  = { verificationId ->
                isLoading = false
                onNavigateToOtp(fullPhone, verificationId)
            },
            onAutoVerified = { credential ->
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) onNavigateToMain()
                    else errorMessage = "Не удалось подтвердить номер. Попробуйте снова"
                }
            },
            onError = { message ->
                isLoading    = false
                errorMessage = message
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
    ) {
        // Dark header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary))),
        ) {
            // Decor
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = (-40).dp)
                    .alpha(0.07f)
                    .background(RelaxWhite, CircleShape)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Brush.linearGradient(listOf(RelaxRed, RelaxOrange)), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("R", color = RelaxWhite, fontSize = 36.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(16.dp))
                Text("RELAX", color = RelaxWhite, fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
                Spacer(Modifier.height(6.dp))
                Text("Супермаркет нового поколения", color = RelaxTextOnDarkSub, fontSize = 13.sp)
            }
        }

        // Form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(RelaxBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 32.dp),
        ) {
            Text("Вход в аккаунт", style = MaterialTheme.typography.headlineLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Введите номер телефона, чтобы войти или зарегистрироваться", color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)

            Spacer(Modifier.height(28.dp))

            // Phone field
            Text("Номер телефона", style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = phone,
                onValueChange = { if (it.length <= 18) phone = it },
                placeholder   = { Text("+992 (__)___-__-__", color = RelaxTextHint) },
                leadingIcon   = {
                    Icon(Icons.Rounded.Phone, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().height(56.dp),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = RelaxWhite,
                    unfocusedContainerColor  = RelaxWhite,
                    focusedBorderColor       = RelaxDark,
                    unfocusedBorderColor     = RelaxDivider,
                    focusedTextColor         = RelaxTextPrimary,
                    unfocusedTextColor       = RelaxTextPrimary,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle     = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(24.dp))

            RelaxButton(
                text      = "Получить код",
                onClick   = { requestVerificationCode() },
                modifier  = Modifier.fillMaxWidth(),
                isLoading = isLoading,
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = errorMessage!!,
                    color      = RelaxError,
                    style      = MaterialTheme.typography.bodySmall,
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = RelaxDivider)
                Text("  или войти через  ", color = RelaxTextHint, style = MaterialTheme.typography.bodySmall)
                Divider(modifier = Modifier.weight(1f), color = RelaxDivider)
            }

            Spacer(Modifier.height(20.dp))

            // Guest mode
            OutlinedButton(
                onClick  = onNavigateToMain,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                border   = BorderStroke(1.5.dp, RelaxDivider),
            ) {
                Icon(Icons.Rounded.Person, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Продолжить как гость", color = RelaxTextSecondary, style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text      = "Нажимая «Получить код», вы соглашаетесь с Пользовательским соглашением и Политикой конфиденциальности RELAX",
                color     = RelaxTextHint,
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )
        }
    }
}
