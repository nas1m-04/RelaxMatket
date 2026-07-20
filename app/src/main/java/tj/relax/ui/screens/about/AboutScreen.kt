package tj.relax.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.relax.BuildConfig
import tj.relax.R
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.components.TelegramLinkRow
import tj.relax.ui.theme.*

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        RelaxTopBar(title = stringResource(R.string.about_title), onBack = onBack)

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.logo_mark),
                contentDescription = null,
                modifier = Modifier.size(88.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text("RELAX", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = RelaxTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
                color = RelaxTextSecondary,
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(18.dp),
                colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        stringResource(R.string.about_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = RelaxTextPrimary,
                        textAlign = TextAlign.Start,
                    )
                    Spacer(Modifier.height(16.dp))
                    RelaxDivider()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.developed),
                        style = MaterialTheme.typography.labelMedium,
                        color = RelaxTextSecondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    TelegramLinkRow()
                }
            }
        }
    }
}
