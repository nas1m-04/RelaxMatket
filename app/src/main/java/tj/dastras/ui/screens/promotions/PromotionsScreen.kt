package tj.dastras.ui.screens.promotions

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.data.MockData
import tj.dastras.data.Promotion
import tj.dastras.ui.components.RelaxTopBar
import tj.dastras.ui.theme.*

@Composable
fun PromotionsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = "Акции", onBack = onBack)
        }

        LazyColumn(
            contentPadding      = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Timer banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFE53935), Color(0xFFFF6B35))))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Timer, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Акции сгорают через", color = RelaxWhite.copy(alpha = 0.85f), fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TimeUnit("02", "дня")
                            Text(":", color = RelaxWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            TimeUnit("14", "часа")
                            Text(":", color = RelaxWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            TimeUnit("33", "мин")
                        }
                    }
                }
            }

            // Coupon
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(RelaxWhite)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center,
                        ) { Text("🎁", fontSize = 28.sp) }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Купон новичка", style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                            Text("−15% на первый заказ", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RelaxDark)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("RELAX15", color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 2.sp)
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.ContentCopy, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            items(MockData.promotions) { promo ->
                PromotionCard(promo)
            }

            // Product promotions
            item {
                Text("Товары со скидкой", style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
            }

            items(MockData.products.filter { it.oldPrice != null }) { product ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(RelaxWhite)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model             = product.imageUrl,
                            contentDescription = product.name,
                            contentScale      = ContentScale.Crop,
                            modifier          = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, maxLines = 2)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${product.price.toInt()} ₽", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RelaxTextPrimary)
                                Text("${product.oldPrice!!.toInt()} ₽", fontSize = 12.sp, color = RelaxTextHint)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RelaxRed)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    val disc = ((1 - product.price / product.oldPrice) * 100).toInt()
                                    Text("−$disc%", color = RelaxWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionCard(promo: Promotion) {
    Card(
        modifier  = Modifier.fillMaxWidth().height(200.dp),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box {
            AsyncImage(
                model             = promo.imageUrl,
                contentDescription = promo.title,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(
                        listOf(Color(promo.backgroundColor).copy(0.9f), Color(promo.backgroundColor).copy(0.3f))
                    ))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(24.dp)
                    .fillMaxWidth(0.65f),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RelaxRed)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(promo.discount, color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(promo.title, color = RelaxWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(promo.subtitle, color = RelaxWhite.copy(alpha = 0.8f), fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Timer, null, tint = RelaxWhite.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(promo.endDate, color = RelaxWhite.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TimeUnit(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(RelaxWhite.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(value, color = RelaxWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        Text(unit, color = RelaxWhite.copy(alpha = 0.65f), fontSize = 10.sp)
    }
}
