package tj.relax.ui.screens.product

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.relax.R
import tj.relax.data.Product
import tj.relax.ui.components.*
import tj.relax.ui.screens.cart.CartViewModel
import tj.relax.ui.screens.favorites.FavoritesViewModel
import tj.relax.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = activityViewModel(),
    favoritesViewModel: FavoritesViewModel = activityViewModel(),
) {
    val state   = viewModel.uiState
    val product = state.product
    val context = LocalContext.current
    val cartState = cartViewModel.uiState
    val favoritesState = favoritesViewModel.uiState

    if (state.isLoading || product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = RelaxDark)
        }
        return
    }

    val images = product.images.ifEmpty { listOfNotNull(product.imageUrl) }
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })
    val discount   = if (product.oldPrice != null) ((1 - product.price / product.oldPrice) * 100).toInt() else 0

    Box(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {

            Box(modifier = Modifier.fillMaxWidth().height(340.dp).background(Color(0xFFECEEF1))) {
                HorizontalPager(state = pagerState) { page ->
                    AsyncImage(
                        model              = images.getOrNull(page),
                        contentDescription = product.name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize(),
                    )
                }
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, RelaxBackground))))
                Row(
                    modifier              = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp).align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(42.dp).shadow(8.dp, CircleShape).clip(CircleShape).background(RelaxWhite)) {
                        Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary, modifier = Modifier.size(16.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick  = { favoritesViewModel.toggle(product) },
                            modifier = Modifier.size(42.dp).shadow(8.dp, CircleShape).clip(CircleShape).background(RelaxWhite)
                        ) {
                            val isFavorite = favoritesState.favorites.any { it.id == product.id }
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint     = if (isFavorite) RelaxRed else RelaxTextSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(42.dp).shadow(8.dp, CircleShape).clip(CircleShape).background(RelaxWhite)) {
                            Icon(Icons.Rounded.Share, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                if (product.hasCardDiscount) {
                    Box(modifier = Modifier.padding(20.dp).clip(RoundedCornerShape(10.dp)).background(RelaxGold).padding(horizontal = 12.dp, vertical = 6.dp).align(Alignment.BottomStart)) {
                        Text(stringResource(R.string.product_card_price_badge), color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                } else if (product.oldPrice != null) {
                    Box(modifier = Modifier.padding(20.dp).clip(RoundedCornerShape(10.dp)).background(RelaxRed).padding(horizontal = 12.dp, vertical = 6.dp).align(Alignment.BottomStart)) {
                        Text(stringResource(R.string.product_discount_badge, discount), color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
                if (images.size > 1) {
                    Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(images.size) { idx ->
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (idx == pagerState.currentPage) RelaxDark else RelaxDivider))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                if (!product.brand.isNullOrEmpty()) {
                    Text(product.brand ?: "", style = MaterialTheme.typography.labelMedium, color = RelaxRed, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                }
                Text(product.name, style = MaterialTheme.typography.headlineMedium, color = RelaxTextPrimary, lineHeight = 28.sp)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (product.rating > 0) RatingRow(product.rating, product.reviewCount)
                    if (!product.weight.isNullOrEmpty()) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(RelaxSurfaceAlt).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text(product.weight ?: "", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "${product.effectivePrice.toInt()} TJS",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (product.hasCardDiscount) RelaxGold else RelaxTextPrimary,
                    )
                    if (product.hasCardDiscount) {
                        Column {
                            Text("${product.price.toInt()} TJS", style = MaterialTheme.typography.bodyLarge, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
                            Text(stringResource(R.string.product_card_price_label), style = MaterialTheme.typography.bodySmall, color = RelaxGold, fontWeight = FontWeight.SemiBold)
                        }
                    } else if (product.oldPrice != null) {
                        Column {
                            Text("${product.oldPrice.toInt()} TJS", style = MaterialTheme.typography.bodyLarge, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
                            Text(stringResource(R.string.product_savings, (product.oldPrice - product.price).toInt()), style = MaterialTheme.typography.bodySmall, color = RelaxSuccess, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                val cartQuantity = cartState.items.find { it.product.id == product.id }?.quantity ?: 0
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(RelaxSurfaceAlt).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick  = { cartViewModel.decrease(product.id) },
                            enabled  = cartQuantity > 0,
                            modifier = Modifier.size(40.dp),
                        ) {
                            Icon(Icons.Rounded.Remove, null, tint = RelaxTextPrimary, modifier = Modifier.size(18.dp))
                        }
                        Text("${cartQuantity.coerceAtLeast(1)}", modifier = Modifier.widthIn(min = 40.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RelaxTextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        IconButton(onClick = { cartViewModel.add(product) }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Rounded.Add, null, tint = RelaxTextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text("${(product.effectivePrice * cartQuantity.coerceAtLeast(1)).toInt()} TJS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RelaxTextPrimary)
                }

                Spacer(Modifier.height(24.dp))
                ProductTabs(product = product, categoryName = state.category?.name)
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, RelaxWhite, RelaxWhite)))
                .navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            val cartQuantity = cartState.items.find { it.product.id == product.id }?.quantity ?: 0
            val addedToCartMessage = stringResource(R.string.product_added_to_cart)
            Button(
                onClick   = {
                    cartViewModel.add(product)
                    Toast.makeText(context, addedToCartMessage, Toast.LENGTH_SHORT).show()
                },
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = RelaxRed),
            ) {
                Icon(Icons.Rounded.ShoppingCart, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(stringResource(R.string.product_add_to_cart_price, (product.effectivePrice * cartQuantity.coerceAtLeast(1)).toInt()), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ProductTabs(product: Product, categoryName: String?) {
    var activeTab by remember { mutableStateOf(0) }
    val tabLabels = listOf(
        stringResource(R.string.product_tab_description),
        stringResource(R.string.product_tab_composition),
        stringResource(R.string.product_tab_reviews),
    )
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RelaxSurfaceAlt).padding(4.dp)) {
        tabLabels.forEachIndexed { idx, label ->
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == idx) RelaxWhite else Color.Transparent)
                    .clickable { activeTab = idx }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, fontSize = 13.sp, fontWeight = if (activeTab == idx) FontWeight.Bold else FontWeight.Normal, color = if (activeTab == idx) RelaxTextPrimary else RelaxTextSecondary)
            }
        }
    }
    Spacer(Modifier.height(20.dp))
    when (activeTab) {
        0 -> {
            Text(product.description.orEmpty().ifEmpty { stringResource(R.string.product_default_description) }, style = MaterialTheme.typography.bodyLarge, color = RelaxTextSecondary, lineHeight = 26.sp)
            Spacer(Modifier.height(16.dp))
            CharacteristicsTable(product = product, categoryName = categoryName)
        }
        1 -> Text(product.composition.orEmpty().ifEmpty { stringResource(R.string.product_no_composition_info) }, style = MaterialTheme.typography.bodyLarge, color = RelaxTextSecondary, lineHeight = 26.sp)
        2 -> ReviewsSection(product.rating, product.reviewCount)
    }
}

@Composable
private fun CharacteristicsTable(product: Product, categoryName: String?) {
    val brandLabel    = stringResource(R.string.product_char_brand)
    val weightLabel   = stringResource(R.string.product_char_weight)
    val unitLabel     = stringResource(R.string.product_char_unit)
    val inStockLabel  = stringResource(R.string.product_char_in_stock)
    val categoryLabel = stringResource(R.string.product_char_category)
    val inStockYes    = stringResource(R.string.product_in_stock_yes)
    val inStockNo     = stringResource(R.string.product_in_stock_no)
    val rows = buildList {
        if (!product.brand.isNullOrEmpty())  add(Pair(brandLabel, product.brand ?: ""))
        if (!product.weight.isNullOrEmpty()) add(Pair(weightLabel, product.weight ?: ""))
        if (!product.unit.isNullOrEmpty()) add(Pair(unitLabel, product.unit ?: ""))
        add(Pair(inStockLabel, if (product.inStock) inStockYes else inStockNo))
        if (categoryName != null) add(Pair(categoryLabel, categoryName))
    }
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        rows.forEachIndexed { idx, (key, value) ->
            Row(
                modifier              = Modifier.fillMaxWidth().background(if (idx % 2 == 0) RelaxSurfaceAlt else RelaxWhite).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(key,   color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(value, color = RelaxTextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ReviewsSection(rating: Double, count: Int) {
    if (count == 0) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.product_no_reviews), color = RelaxTextSecondary)
        }
        return
    }
    val reviewNames = listOf(
        stringResource(R.string.product_review_name_1),
        stringResource(R.string.product_review_name_2),
        stringResource(R.string.product_review_name_3),
    )
    val reviewTexts = listOf(
        stringResource(R.string.product_review_text_1),
        stringResource(R.string.product_review_text_2),
        stringResource(R.string.product_review_text_3),
    )
    val reviewDates = listOf(
        stringResource(R.string.product_review_date_1),
        stringResource(R.string.product_review_date_2),
        stringResource(R.string.product_review_date_3),
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(rating.toString(), fontSize = 48.sp, fontWeight = FontWeight.Black, color = RelaxTextPrimary)
                Row { repeat(5) { i -> Icon(if (i < rating.toInt()) Icons.Rounded.Star else Icons.Rounded.StarBorder, null, tint = Color(0xFFFBBC04), modifier = Modifier.size(18.dp)) } }
                Text(stringResource(R.string.product_reviews_count, count), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
            }
        }
        repeat(3) { idx ->
            ReviewItem(
                name   = reviewNames[idx],
                rating = listOf(5, 4, 5)[idx],
                text   = reviewTexts[idx],
                date   = reviewDates[idx],
            )
        }
    }
}

@Composable
private fun ReviewItem(name: String, rating: Int, text: String, date: String) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(RelaxWhite).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(RelaxDark), contentAlignment = Alignment.Center) {
                    Text(name.first().toString(), color = RelaxWhite, fontWeight = FontWeight.Bold)
                }
                Text(name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
            }
            Text(date, style = MaterialTheme.typography.bodySmall, color = RelaxTextHint)
        }
        Spacer(Modifier.height(6.dp))
        Row { repeat(5) { i -> Icon(if (i < rating) Icons.Rounded.Star else Icons.Rounded.StarBorder, null, tint = Color(0xFFFBBC04), modifier = Modifier.size(14.dp)) } }
        Spacer(Modifier.height(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary, lineHeight = 20.sp)
    }
}
