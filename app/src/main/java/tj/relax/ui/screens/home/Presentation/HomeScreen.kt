package tj.relax.ui.screens.home.Presentation

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import tj.relax.data.Notification
import tj.relax.data.NotificationType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.relax.R
import tj.relax.ui.components.*
import tj.relax.ui.screens.cart.CartViewModel
import tj.relax.ui.screens.favorites.FavoritesViewModel
import tj.relax.ui.theme.*
import kotlinx.coroutines.delay
import tj.relax.data.Banner
import tj.relax.data.Category
import tj.relax.ui.screens.home.ViewModel.HomeViewModel

private fun parseHexColor(hex: String?, default: Color = Color(0xFFF3F4F6)): Color {
    if (hex.isNullOrBlank()) return default
    return try {
        val normalized = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(normalized))
    } catch (e: Exception) {
        default
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProduct: (Int) -> Unit,
    onCart: () -> Unit,
    onNotifications: () -> Unit,
    onPromotions: () -> Unit,
    onFavorites: () -> Unit,
    onSearch: () -> Unit,
    onCategory: (Int) -> Unit,
    onSeeAllPopular: () -> Unit,
    onSeeAllNew: () -> Unit,
    onSeeAllBestOffers: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = activityViewModel(),
    favoritesViewModel: FavoritesViewModel = activityViewModel(),
) {
    val state           = viewModel.uiState
    val cartState       = cartViewModel.uiState
    val favoritesState  = favoritesViewModel.uiState
    val bannerList      = state.banners
    val bannerPager     = rememberPagerState(pageCount = { bannerList.size.coerceAtLeast(1) })

    LaunchedEffect(bannerList.size) {
        if (bannerList.size > 1) {
            while (true) {
                delay(3500)
                val next = (bannerPager.currentPage + 1) % bannerList.size
                bannerPager.animateScrollToPage(next)
            }
        }
    }

    var showAddressSheet by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    // Marking notifications read happens on a separate screen — re-check the unread dot whenever
    // the user comes back here (e.g. from the notifications list), not just on first load.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshUnreadCount()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ── IN-APP MODAL ─────────────────────────────────────────────────────────
    state.inAppModal?.let { modal ->
        InAppMessageDialog(
            notification = modal,
            onDismiss    = { viewModel.dismissInAppModal() },
        )
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh    = { viewModel.load(forceRefresh = true) },
        state        = pullState,
        modifier     = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── ЗАКРЕПЛЁННЫЙ TOP BAR ─────────────────────────────────────────
            HomeTopBar(
                address = state.deliveryAddress,
                onAddressClick = { showAddressSheet = true },
                onNotifications = onNotifications,
                onCart = onCart,
                onFavorites = onFavorites,
                hasUnread = state.hasUnread,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RelaxBackground),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {

                // ── SEARCH BAR ─────────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RelaxBackground)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {

                        Card(
                            onClick = onSearch,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = RelaxWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            RelaxBackground,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.Search,
                                        contentDescription = null,
                                        tint = RelaxTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = stringResource(R.string.home_search_placeholder),
                                    color = RelaxTextHint,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // ── SKELETON (first load only) ──────────────────────────────────
                if (state.isLoading && state.isEmpty) {
                    item { HomeScreenSkeleton() }
                }

                // ── BANNERS ─────────────────────────────────────────────────────────
                if (bannerList.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            HorizontalPager(
                                state = bannerPager,
                                pageSpacing = 14.dp,
                            ) { page ->
                                BannerCard(banner = bannerList[page], onClick = onPromotions)
                            }
                            Spacer(Modifier.height(14.dp))
                            // Indicator dots
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                repeat(bannerList.size) { idx ->
                                    val isSelected = idx == bannerPager.currentPage
                                    val dotWidth by animateDpAsState(
                                        targetValue = if (isSelected) 22.dp else 6.dp,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "dot_width",
                                    )
                                    val dotColor by animateColorAsState(
                                        targetValue = if (isSelected) RelaxDark else RelaxDivider,
                                        label = "dot_color",
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .height(6.dp)
                                            .width(dotWidth)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(dotColor)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                }
                            }
                        }
                    }
                }

                // ── CATEGORIES ──────────────────────────────────────────────────────
                if (state.categories.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 28.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.home_section_categories),
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(state.categories) { cat ->
                                    CategoryChip(category = cat, onClick = { onCategory(cat.id) })
                                }
                            }
                        }
                    }
                }

                // ── POPULAR PRODUCTS ────────────────────────────────────────────────
                if (state.products.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 32.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.home_section_popular),
                                onSeeAll = onSeeAllPopular,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            LazyHorizontalGrid(
                                rows = GridCells.Fixed(1),
                                modifier = Modifier.height(290.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(state.products.take(6)) { product ->
                                    ProductCardGrid(
                                        product = product,
                                        onClick = { onProduct(product.id) },
                                        quantity = cartState.items.find { it.product.id == product.id }?.quantity
                                            ?: 0,
                                        onIncrease = { cartViewModel.add(product) },
                                        onDecrease = { cartViewModel.decrease(product.id) },
                                        isFavorite = favoritesState.favorites.any { it.id == product.id },
                                        onToggleFavorite = { favoritesViewModel.toggle(product) },
                                        modifier = Modifier.width(178.dp),
                                    )
                                }
                            }
                        }
                    }

                    // Promo strip between sections
                    item {
                        Spacer(Modifier.height(32.dp))
                        PromoStrip(onClick = onPromotions)
                    }
                }

                // ── NEW PRODUCTS ────────────────────────────────────────────────────
                if (state.newProducts.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 32.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.home_section_new),
                                onSeeAll = onSeeAllNew,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(state.newProducts) { product ->
                                    ProductCardHorizontal(
                                        product    = product,
                                        onClick    = { onProduct(product.id) },
                                        quantity   = cartState.items.find { it.product.id == product.id }?.quantity ?: 0,
                                        onIncrease = { cartViewModel.add(product) },
                                        onDecrease = { cartViewModel.decrease(product.id) },
                                    )
                                }
                            }
                        }
                    }
                }

                // ── SALE PRODUCTS ───────────────────────────────────────────────────
                if (state.saleProducts.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 32.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.home_section_sale_week),
                                onSeeAll = onPromotions,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                            Spacer(Modifier.height(16.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(state.saleProducts) { product ->
                                    ProductCardHorizontal(
                                        product    = product,
                                        onClick    = { onProduct(product.id) },
                                        quantity   = cartState.items.find { it.product.id == product.id }?.quantity ?: 0,
                                        onIncrease = { cartViewModel.add(product) },
                                        onDecrease = { cartViewModel.decrease(product.id) },
                                    )
                                }
                            }
                        }
                    }
                }

                // ── BEST OFFERS GRID ────────────────────────────────────────────────
                if (state.products.size > 6) {
                    item {
                        Spacer(Modifier.height(32.dp))
                        SectionHeader(
                            title = stringResource(R.string.home_section_best_offers),
                            onSeeAll = onSeeAllBestOffers,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 820.dp)
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            userScrollEnabled = false,
                        ) {
                            items(state.products.takeLast(6)) { product ->
                                ProductCardGrid(
                                    product = product,
                                    onClick = { onProduct(product.id) },
                                    quantity = cartState.items.find { it.product.id == product.id }?.quantity
                                        ?: 0,
                                    onIncrease = { cartViewModel.add(product) },
                                    onDecrease = { cartViewModel.decrease(product.id) },
                                    isFavorite = favoritesState.favorites.any { it.id == product.id },
                                    onToggleFavorite = { favoritesViewModel.toggle(product) },
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    if (showAddressSheet) {
        AddressBottomSheet(
            currentAddress = state.deliveryAddress,
            onSave         = { viewModel.setDeliveryAddress(it) },
            onDismiss      = { showAddressSheet = false },
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// TOP BAR
// ────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    address: String,
    onAddressClick: () -> Unit,
    onNotifications: () -> Unit,
    onCart: () -> Unit,
    onFavorites: () -> Unit,
    hasUnread: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp, bottom = 18.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Brand + Address
            Column(modifier = Modifier.weight(1f)) {
                // Logo row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.linearGradient(listOf(RelaxDark, RelaxDarkSecondary)),
                                RoundedCornerShape(11.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = "R",
                            color      = RelaxWhite,
                            fontSize   = 21.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text          = "RELAX",
                        color         = RelaxWhite,
                        fontSize      = 19.sp,
                        fontWeight    = FontWeight.Black,
                        letterSpacing = 3.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Address chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(RelaxWhite.copy(alpha = 0.10f))
                        .clickable(onClick = onAddressClick)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint     = RelaxRed,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text     = address.ifBlank { stringResource(R.string.home_address_not_set) },
                        color    = if (address.isBlank()) RelaxTextOnDarkSub.copy(alpha = 0.5f) else RelaxTextOnDarkSub,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 170.dp),
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint     = RelaxTextOnDarkSub.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            // Action buttons — separated by explicit spacing
            Spacer(Modifier.width(12.dp))
            HomeIconButton(
                icon    = Icons.Rounded.FavoriteBorder,
                badge   = false,
                onClick = onFavorites,
            )
            Spacer(Modifier.width(8.dp))
            HomeIconButton(
                icon    = Icons.Rounded.NotificationsNone,
                badge   = hasUnread,
                onClick = onNotifications,
            )
            Spacer(Modifier.width(8.dp))
            HomeIconButton(
                icon    = Icons.Rounded.ShoppingCart,
                badge   = false,
                onClick = onCart,
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ICON BUTTON
// ────────────────────────────────────────────────────────────────────────────
@Composable
private fun HomeIconButton(
    icon: ImageVector,
    badge: Boolean,
    onClick: () -> Unit,
) {
    Box {
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(RelaxWhite.copy(alpha = 0.13f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = RelaxWhite,
                modifier           = Modifier.size(22.dp),
            )
        }
        if (badge) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-3).dp, y = 3.dp)
                    .background(RelaxRed, CircleShape)
                    .border(1.5.dp, RelaxDark, CircleShape)
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// BANNER CARD
// ────────────────────────────────────────────────────────────────────────────
@Composable
private fun BannerCard(banner: Banner, onClick: () -> Unit) {
    Card(
        onClick   = onClick,
        modifier  = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = Color(0x22000000)),
        shape     = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFD8DCE0))) {
            AsyncImage(
                model              = banner.imageUrl,
                contentDescription = banner.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
            val bannerColor = parseHexColor(banner.backgroundColor, default = Color(0xFF1C1C1E))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                bannerColor.copy(alpha = 0.94f),
                                bannerColor.copy(alpha = 0.35f),
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 26.dp, end = 100.dp, top = 24.dp, bottom = 24.dp),
            ) {
                if (!banner.badgeText.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RelaxWhite.copy(alpha = 0.20f),
                    ) {
                        Text(
                            text     = banner.badgeText,
                            color    = RelaxWhite,
                            style    = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Text(
                    text       = banner.title,
                    color      = RelaxWhite,
                    fontSize   = 21.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 27.sp,
                )
                if (!banner.subtitle.isNullOrEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = banner.subtitle,
                        color      = RelaxWhite.copy(alpha = 0.82f),
                        fontSize   = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// CATEGORY CHIP
// ────────────────────────────────────────────────────────────────────────────
@Composable
private fun CategoryChip(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(88.dp)
            .height(96.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FB)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.icon ?: "📦",
                fontSize = 28.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = RelaxTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// PROMO STRIP
// ────────────────────────────────────────────────────────────────────────────
@Composable
private fun PromoStrip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(listOf(RelaxRed, Color(0xFFB0262B)))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 22.dp),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = stringResource(R.string.home_promo_title),
                    color      = RelaxWhite,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = stringResource(R.string.home_promo_subtitle),
                    color = RelaxWhite.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(RelaxWhite.copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint     = RelaxWhite,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ADDRESS BOTTOM SHEET
// ────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressBottomSheet(
    currentAddress: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var address by remember(currentAddress) { mutableStateOf(currentAddress) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = RelaxWhite,
        shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 36.dp)
                .navigationBarsPadding(),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Brush.linearGradient(listOf(RelaxDark, RelaxDarkSecondary))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint     = RelaxWhite,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text       = stringResource(R.string.home_address_sheet_title),
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = RelaxTextPrimary,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = stringResource(R.string.home_address_sheet_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = RelaxTextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider(color = RelaxDivider, thickness = 0.5.dp)
            Spacer(Modifier.height(24.dp))

            // Text field
            OutlinedTextField(
                value         = address,
                onValueChange = { address = it },
                label         = { Text(stringResource(R.string.checkout_delivery_address_label)) },
                placeholder   = {
                    Text(
                        text     = stringResource(R.string.checkout_address_placeholder),
                        fontSize = 13.sp,
                    )
                },
                leadingIcon  = {
                    Icon(Icons.Rounded.Home, contentDescription = null, tint = RelaxRed)
                },
                trailingIcon = {
                    if (address.isNotEmpty()) {
                        IconButton(onClick = { address = "" }) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = null,
                                tint     = RelaxTextSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                },
                singleLine = true,
                modifier   = Modifier.fillMaxWidth(),
                shape      = RoundedCornerShape(16.dp),
                colors     = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = RelaxDark,
                    unfocusedBorderColor    = RelaxDivider,
                    focusedContainerColor   = RelaxWhite,
                    unfocusedContainerColor = RelaxInputBg,
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color      = RelaxTextPrimary,
                    fontWeight = FontWeight.Medium,
                ),
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text  = stringResource(R.string.home_address_hint),
                style = MaterialTheme.typography.bodySmall,
                color = RelaxTextHint,
            )

            Spacer(Modifier.height(28.dp))

            // Save button
            Button(
                onClick  = {
                    if (address.isNotBlank()) {
                        onSave(address)
                        onDismiss()
                    }
                },
                enabled  = address.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape  = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = RelaxDark,
                    disabledContainerColor = RelaxDark.copy(alpha = 0.35f),
                ),
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = stringResource(R.string.home_address_save),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// IN-APP MESSAGE DIALOG
// ────────────────────────────────────────────────────────────────────────────
@Composable
private fun InAppMessageDialog(
    notification: Notification,
    onDismiss: () -> Unit,
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val scale by animateFloatAsState(
        targetValue    = if (appeared) 1f else 0.82f,
        animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label          = "dialogScale",
    )
    val alpha by animateFloatAsState(
        targetValue   = if (appeared) 1f else 0f,
        animationSpec = tween(280),
        label         = "dialogAlpha",
    )

    val (badgeLabel, badgeColors) = when (notification.type) {
        NotificationType.PROMO  -> "АКЦИЯ"       to listOf(RelaxRed, Color(0xFFB0262B))
        NotificationType.NEWS   -> "НОВОСТЬ"      to listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
        NotificationType.BONUS  -> "БОНУСЫ"       to listOf(Color(0xFFD4AF37), Color(0xFFF59E0B))
        NotificationType.ORDER  -> "ЗАКАЗ"        to listOf(Color(0xFF1976D2), Color(0xFF0EA5E9))
        NotificationType.SYSTEM -> "СООБЩЕНИЕ"    to listOf(Color(0xFF374151), Color(0xFF6B7280))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .scale(scale)
                .alpha(alpha),
        ) {
            Card(
                shape     = RoundedCornerShape(28.dp),
                colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column {

                    // ── Image ──────────────────────────────────────────────
                    if (!notification.imageUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            AsyncImage(
                                model              = notification.imageUrl,
                                contentDescription = null,
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                            )
                            // Bottom gradient for smooth transition into card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, RelaxWhite)
                                        )
                                    )
                            )
                        }
                    }

                    // ── Content ────────────────────────────────────────────
                    Column(
                        modifier = Modifier.padding(
                            start  = 28.dp,
                            end    = 28.dp,
                            top    = if (notification.imageUrl.isNullOrBlank()) 32.dp else 4.dp,
                            bottom = 28.dp,
                        )
                    ) {
                        // Badge pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.horizontalGradient(badgeColors))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text          = badgeLabel,
                                color         = RelaxWhite,
                                fontSize      = 10.sp,
                                fontWeight    = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text       = notification.title,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = RelaxTextPrimary,
                            lineHeight = 29.sp,
                        )

                        if (notification.body.isNotBlank()) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text       = notification.body,
                                fontSize   = 14.sp,
                                color      = RelaxTextSecondary,
                                lineHeight = 22.sp,
                            )
                        }

                        Spacer(Modifier.height(26.dp))

                        // Gradient CTA button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(badgeColors))
                                .clickable(onClick = onDismiss)
                                .padding(vertical = 15.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text       = "Понятно",
                                color      = RelaxWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp,
                                letterSpacing = 0.3.sp,
                            )
                        }
                    }
                }
            }

            // ── Floating close button ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(34.dp)
                    .clip(RoundedCornerShape(50))
                    .background(RelaxDark.copy(alpha = 0.55f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint     = RelaxWhite,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}