package tj.dastras.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.data.MockData
import tj.dastras.ui.components.*
import tj.dastras.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onProduct: (Int) -> Unit,
    onCart: () -> Unit,
    onNotifications: () -> Unit,
    onPromotions: () -> Unit,
    onFavorites: () -> Unit,
) {
    var searchQuery   by remember { mutableStateOf("") }
    val bannerPager   = rememberPagerState(pageCount = { MockData.banners.size })

    // Auto-scroll banner
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            val next = (bannerPager.currentPage + 1) % MockData.banners.size
            bannerPager.animateScrollToPage(next)
        }
    }

    LazyColumn(
        modifier                  = Modifier.fillMaxSize().background(RelaxBackground),
        contentPadding            = PaddingValues(bottom = 24.dp),
        verticalArrangement       = Arrangement.spacedBy(0.dp),
    ) {
        // ── Top Bar ────────────────────────────────────────────
        item {
            HomeTopBar(onNotifications = onNotifications, onCart = onCart, onFavorites = onFavorites)
        }

        // ── Search Bar ─────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(RelaxDark, RelaxBackground), startY = 0f, endY = 60f))
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
            ) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Search, null, tint = RelaxTextSecondary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Поиск товаров...", color = RelaxTextHint, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // ── Banner Carousel ────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                HorizontalPager(state = bannerPager, pageSpacing = 12.dp) { page ->
                    BannerCard(banner = MockData.banners[page], onClick = onPromotions)
                }
                Spacer(Modifier.height(12.dp))
                // Page indicator
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    repeat(MockData.banners.size) { idx ->
                        val isSelected = idx == bannerPager.currentPage
                        val width by animateDpAsState(if (isSelected) 24.dp else 6.dp, label = "dot")
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(width)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isSelected) RelaxDark else RelaxDivider)
                        )
                    }
                }
            }
        }

        // ── Categories ─────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                SectionHeader(
                    title = "Категории",
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(14.dp))
                LazyRow(
                    contentPadding      = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(MockData.categories) { cat ->
                        CategoryChip(category = cat, onClick = {})
                    }
                }
            }
        }

        // ── Popular ────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                SectionHeader(
                    title    = "Популярное",
                    onSeeAll = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(14.dp))
                LazyHorizontalGrid(
                    rows             = GridCells.Fixed(1),
                    modifier         = Modifier.height(280.dp),
                    contentPadding   = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(MockData.products.take(6)) { product ->
                        ProductCardGrid(
                            product     = product,
                            onClick     = { onProduct(product.id) },
                            onAddToCart = {},
                            modifier    = Modifier.width(175.dp),
                        )
                    }
                }
            }
        }

        // ── Promo Strip ────────────────────────────────────────
        item {
            Spacer(Modifier.height(28.dp))
            PromoStrip(onClick = onPromotions)
        }

        // ── New Arrivals ───────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                SectionHeader(
                    title    = "Новинки",
                    onSeeAll = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(14.dp))
                LazyRow(
                    contentPadding      = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(MockData.products.filter { it.isNew }) { product ->
                        ProductCardHorizontal(
                            product     = product,
                            onClick     = { onProduct(product.id) },
                            onAddToCart = {},
                        )
                    }
                }
            }
        }

        // ── For You ────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                SectionHeader(
                    title    = "Акции недели",
                    onSeeAll = onPromotions,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(14.dp))
                LazyRow(
                    contentPadding      = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(MockData.products.filter { it.oldPrice != null }) { product ->
                        ProductCardHorizontal(
                            product     = product,
                            onClick     = { onProduct(product.id) },
                            onAddToCart = {},
                        )
                    }
                }
            }
        }

        // ── Featured Products Grid ─────────────────────────────
        item {
            Spacer(Modifier.height(28.dp))
            SectionHeader(
                title    = "Лучшие предложения",
                onSeeAll = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(14.dp))
        }

        item {
            LazyVerticalGrid(
                columns             = GridCells.Fixed(2),
                modifier            = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 800.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled    = false,
            ) {
                items(MockData.products.takeLast(6)) { product ->
                    ProductCardGrid(
                        product     = product,
                        onClick     = { onProduct(product.id) },
                        onAddToCart = {},
                    )
                }
            }
        }
    }
}

// ── Home Top Bar ───────────────────────────────────────────────
@Composable
private fun HomeTopBar(onNotifications: () -> Unit, onCart: () -> Unit, onFavorites: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Logo & Address
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Brush.linearGradient(listOf(RelaxRed, RelaxOrange)), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("R", color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("RELAX", color = RelaxWhite, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocationOn, null, tint = RelaxOrange, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("ул. Ленина, 45", color = RelaxTextOnDarkSub, fontSize = 12.sp)
                    Icon(Icons.Rounded.KeyboardArrowDown, null, tint = RelaxTextOnDarkSub, modifier = Modifier.size(14.dp))
                }
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeIconButton(icon = Icons.Rounded.FavoriteBorder, badge = false, onClick = onFavorites)
                HomeIconButton(icon = Icons.Rounded.NotificationsNone, badge = true, onClick = onNotifications)
                HomeIconButton(icon = Icons.Rounded.ShoppingCart, badge = false, onClick = onCart)
            }
        }
    }
}

@Composable
private fun HomeIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, badge: Boolean, onClick: () -> Unit) {
    Box {
        IconButton(
            onClick  = onClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(RelaxWhite.copy(alpha = 0.12f))
        ) {
            Icon(icon, null, tint = RelaxWhite, modifier = Modifier.size(22.dp))
        }
        if (badge) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .background(RelaxRed, CircleShape)
            )
        }
    }
}

// ── Banner Card ────────────────────────────────────────────────
@Composable
private fun BannerCard(banner: tj.dastras.data.Banner, onClick: () -> Unit) {
    Card(
        onClick   = onClick,
        modifier  = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model             = banner.imageUrl,
                contentDescription = banner.title,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier.fillMaxSize(),
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(banner.backgroundColor).copy(alpha = 0.92f),
                                Color(banner.backgroundColor).copy(alpha = 0.4f),
                            )
                        )
                    )
            )
            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(24.dp)
                    .fillMaxWidth(0.65f),
            ) {
                if (banner.badgeText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RelaxWhite.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(banner.badgeText, color = RelaxWhite, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Text(banner.title, color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                Spacer(Modifier.height(6.dp))
                Text(banner.subtitle, color = RelaxWhite.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

// ── Category Chip ──────────────────────────────────────────────
@Composable
private fun CategoryChip(category: tj.dastras.data.Category, onClick: () -> Unit) {
    Column(
        modifier            = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(category.color))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(category.icon, fontSize = 28.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            text      = category.name,
            fontSize  = 11.sp,
            fontWeight = FontWeight.Medium,
            color     = RelaxTextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Promo Strip ────────────────────────────────────────────────
@Composable
private fun PromoStrip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE53935), Color(0xFFFF6B35))))
            .clickable(onClick = onClick)
            .padding(24.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("🔥 Акции недели", color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Скидки до 40% на сотни товаров", color = RelaxWhite.copy(alpha = 0.88f), fontSize = 13.sp)
            }
            Icon(Icons.Rounded.ArrowForwardIos, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
        }
    }
}
