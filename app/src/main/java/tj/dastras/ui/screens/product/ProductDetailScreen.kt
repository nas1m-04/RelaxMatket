package tj.dastras.ui.screens.product

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.data.MockData
import tj.dastras.ui.components.*
import tj.dastras.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(productId: Int, onBack: () -> Unit, onCart: () -> Unit) {
    val product      = MockData.products.find { it.id == productId } ?: MockData.products.first()
    var isFavorite   by remember { mutableStateOf(product.isFavorite) }
    var count        by remember { mutableStateOf(0) }
    var activeTab    by remember { mutableStateOf(0) }

    // Multiple image seeds for gallery
    val images = listOf(product.imageUrl, "https://picsum.photos/seed/${product.id}a/400/400", "https://picsum.photos/seed/${product.id}b/400/400")
    val pagerState = rememberPagerState(pageCount = { images.size })

    val discount = if (product.oldPrice != null) ((1 - product.price / product.oldPrice) * 100).toInt() else 0

    Box(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {

            // ── Photo Gallery ──────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
                HorizontalPager(state = pagerState) { page ->
                    AsyncImage(
                        model             = images[page],
                        contentDescription = product.name,
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize(),
                    )
                }

                // Gradient overlay (bottom)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, RelaxBackground)))
                )

                // Top actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick  = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(RelaxWhite)
                    ) {
                        Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary, modifier = Modifier.size(16.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick  = { isFavorite = !isFavorite },
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(RelaxWhite)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint     = if (isFavorite) RelaxRed else RelaxTextSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(
                            onClick  = {},
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(RelaxWhite)
                        ) {
                            Icon(Icons.Rounded.Share, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Discount badge
                if (product.oldPrice != null) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RelaxRed)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Text("−$discount%", color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }

                // Page dots
                Row(
                    modifier              = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    repeat(images.size) { idx ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (idx == pagerState.currentPage) RelaxDark else RelaxDivider)
                        )
                    }
                }
            }

            // ── Product Info ───────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                if (product.brand.isNotEmpty()) {
                    Text(product.brand, style = MaterialTheme.typography.labelMedium, color = RelaxRed, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                }
                Text(product.name, style = MaterialTheme.typography.headlineMedium, color = RelaxTextPrimary, lineHeight = 28.sp)

                Spacer(Modifier.height(12.dp))

                // Rating & weight
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (product.rating > 0) {
                        RatingRow(product.rating, product.reviewCount)
                    }
                    if (product.weight.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RelaxSurfaceAlt)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(product.weight, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Price
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text       = "${product.price.toInt()} ₽",
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Black,
                        color      = RelaxTextPrimary,
                    )
                    if (product.oldPrice != null) {
                        Column {
                            Text(
                                text           = "${product.oldPrice.toInt()} ₽",
                                style          = MaterialTheme.typography.bodyLarge,
                                color          = RelaxTextHint,
                                textDecoration = TextDecoration.LineThrough,
                            )
                            Text(
                                "Экономия ${(product.oldPrice - product.price).toInt()} ₽",
                                style = MaterialTheme.typography.bodySmall,
                                color = RelaxSuccess,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Quantity selector
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(RelaxSurfaceAlt)
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { if (count > 0) count-- }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Rounded.Remove, null, tint = RelaxTextPrimary, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text       = "$count",
                            modifier   = Modifier.widthIn(min = 40.dp),
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = RelaxTextPrimary,
                        )
                        IconButton(onClick = { count++ }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Rounded.Add, null, tint = RelaxTextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(
                        "${(product.price * count.coerceAtLeast(1)).toInt()} ₽",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = RelaxTextPrimary,
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RelaxSurfaceAlt)
                        .padding(4.dp),
                ) {
                    listOf("Описание", "Состав", "Отзывы").forEachIndexed { idx, label ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeTab == idx) RelaxWhite else Color.Transparent)
                                .clickable { activeTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text       = label,
                                fontSize   = 13.sp,
                                fontWeight = if (activeTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color      = if (activeTab == idx) RelaxTextPrimary else RelaxTextSecondary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                when (activeTab) {
                    0 -> {
                        Text(
                            text       = if (product.description.isNotEmpty()) product.description else "Высококачественный продукт от проверенного производителя. Идеальный выбор для здорового питания.",
                            style      = MaterialTheme.typography.bodyLarge,
                            color      = RelaxTextSecondary,
                            lineHeight = 26.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        CharacteristicsTable(product)
                    }
                    1 -> {
                        Text(
                            text       = if (product.composition.isNotEmpty()) product.composition else "Информация о составе отсутствует.",
                            style      = MaterialTheme.typography.bodyLarge,
                            color      = RelaxTextSecondary,
                            lineHeight = 26.sp,
                        )
                    }
                    2 -> ReviewsSection(product.rating, product.reviewCount)
                }
            }
        }

        // ── Fixed Bottom Button ────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, RelaxWhite, RelaxWhite)))
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick   = { if (count == 0) count = 1; onCart() },
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = RelaxRed),
            ) {
                Icon(Icons.Rounded.ShoppingCart, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("В корзину · ${(product.price * count.coerceAtLeast(1)).toInt()} ₽", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun CharacteristicsTable(product: tj.dastras.data.Product) {
    val rows = buildList {
        if (product.brand.isNotEmpty())  add(Pair("Бренд", product.brand))
        if (product.weight.isNotEmpty()) add(Pair("Масса / Объём", product.weight))
        add(Pair("Единица", product.unit))
        add(Pair("В наличии", if (product.inStock) "Есть" else "Нет"))
        if (product.categoryId > 0) {
            val cat = tj.dastras.data.MockData.categories.find { it.id == product.categoryId }
            if (cat != null) add(Pair("Категория", cat.name))
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        rows.forEachIndexed { idx, (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (idx % 2 == 0) RelaxSurfaceAlt else RelaxWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(key,   color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(value, color = RelaxTextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ReviewsSection(rating: Float, count: Int) {
    if (count == 0) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("Отзывов пока нет", color = RelaxTextSecondary)
        }
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Summary
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(rating.toString(), fontSize = 48.sp, fontWeight = FontWeight.Black, color = RelaxTextPrimary)
                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < rating.toInt()) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = null,
                            tint     = Color(0xFFFBBC04),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Text("$count отзывов", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
            }
        }

        // Sample reviews
        repeat(3) { idx ->
            ReviewItem(
                name    = listOf("Анна К.", "Михаил П.", "Ольга С.")[idx],
                rating  = listOf(5, 4, 5)[idx],
                text    = listOf("Отличный продукт, покупаю регулярно!", "Хорошее качество, буду брать ещё.", "Свежий, вкусный, рекомендую!")[idx],
                date    = listOf("2 июня", "28 мая", "20 мая")[idx],
            )
        }
    }
}

@Composable
private fun ReviewItem(name: String, rating: Int, text: String, date: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(RelaxWhite)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RelaxDark),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(name.first().toString(), color = RelaxWhite, fontWeight = FontWeight.Bold)
                }
                Text(name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
            }
            Text(date, style = MaterialTheme.typography.bodySmall, color = RelaxTextHint)
        }
        Spacer(Modifier.height(6.dp))
        Row {
            repeat(5) { i ->
                Icon(
                    imageVector = if (i < rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = null,
                    tint     = Color(0xFFFBBC04),
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary, lineHeight = 20.sp)
    }
}
