package tj.relax.ui.screens.promotions

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import org.koin.compose.viewmodel.koinViewModel
import coil.compose.AsyncImage
import tj.relax.R
import tj.relax.data.Product
import tj.relax.data.Promotion
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.screens.cart.CartViewModel
import tj.relax.ui.components.activityViewModel
import tj.relax.ui.theme.*

@Composable
fun PromotionsScreen(
    onBack: () -> Unit,
    onProduct: (Int) -> Unit,
    viewModel: PromotionsViewModel = koinViewModel(),
    cartViewModel: CartViewModel = activityViewModel(),
) {
    val state     = viewModel.uiState
    val cartState = cartViewModel.uiState

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(R.string.promo_title), onBack = onBack)
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { viewModel.load() }) { Text("Повторить") }
                }
            }
            return@Column
        }

        LazyColumn(
            contentPadding      = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.promotions, key = { it.id }) { promo ->
                PromotionCard(promo)
            }

            if (state.saleProducts.isNotEmpty()) {
                item {
                    Text(
                        text  = stringResource(R.string.promo_discounted_products_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = RelaxTextPrimary,
                    )
                }

                items(state.saleProducts, key = { it.id }) { product ->
                    val quantity = cartState.items.find { it.product.id == product.id }?.quantity ?: 0
                    SaleProductCard(
                        product    = product,
                        quantity   = quantity,
                        onClick    = { onProduct(product.id) },
                        onIncrease = { cartViewModel.add(product) },
                        onDecrease = { cartViewModel.decrease(product.id) },
                    )
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
        Box(modifier = Modifier.background(Color(0xFFECEEF1))) {
            AsyncImage(
                model              = promo.imageUrl,
                contentDescription = promo.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
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
                if (promo.discount.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RelaxRed)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(promo.discount, color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Text(promo.title, color = RelaxWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (promo.subtitle.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(promo.subtitle, color = RelaxWhite.copy(alpha = 0.8f), fontSize = 13.sp)
                }
                if (promo.endDate.isNotEmpty()) {
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
}

@Composable
private fun SaleProductCard(
    product: Product,
    quantity: Int,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model              = product.imageUrl,
                contentDescription = product.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFECEEF1)),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, maxLines = 2)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${product.effectivePrice.toInt()} TJS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (product.hasCardDiscount) RelaxGold else RelaxTextPrimary,
                    )
                    if (product.hasCardDiscount) {
                        Text(
                            text = "${product.price.toInt()} TJS",
                            fontSize = 12.sp,
                            color = RelaxTextHint,
                            textDecoration = TextDecoration.LineThrough,
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RelaxGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(stringResource(R.string.product_card_price_badge), color = RelaxWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (product.oldPrice != null) {
                        Text(
                            text = "${product.oldPrice.toInt()} TJS",
                            fontSize = 12.sp,
                            color = RelaxTextHint,
                            textDecoration = TextDecoration.LineThrough,
                        )
                        val disc = ((1 - product.price / product.oldPrice) * 100).toInt()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RelaxRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("−$disc%", color = RelaxWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            // Add-to-cart stepper — disabled, no delivery/pickup cart flow
            /*
            Spacer(Modifier.width(8.dp))
            if (quantity == 0) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RelaxDark)
                        .clickable(onClick = onIncrease),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(16.dp))
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(RelaxDark)
                        .padding(horizontal = 4.dp),
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Remove, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                    }
                    Text("$quantity", color = RelaxWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                    }
                }
            }
            */
        }
    }
}
