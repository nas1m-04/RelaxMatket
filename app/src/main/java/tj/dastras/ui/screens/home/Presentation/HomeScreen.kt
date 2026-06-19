package tj.dastras.ui.screens.home.Presentation

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
import tj.dastras.R
import tj.dastras.ui.components.*
import tj.dastras.ui.screens.cart.CartViewModel
import tj.dastras.ui.screens.favorites.FavoritesViewModel
import tj.dastras.ui.theme.*
import kotlinx.coroutines.delay
import tj.dastras.data.Banner
import tj.dastras.data.Category
import tj.dastras.ui.screens.home.ViewModel.HomeViewModel

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
    val state      = viewModel.uiState
    val cartState  = cartViewModel.uiState
    val favoritesState = favoritesViewModel.uiState
    val bannerList = state.banners
    val bannerPager = rememberPagerState(pageCount = { bannerList.size.coerceAtLeast(1) })

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

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh    = { viewModel.load() },
        state        = pullState,
        modifier     = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier              = Modifier.fillMaxSize().background(RelaxBackground),
            contentPadding        = PaddingValues(bottom = 24.dp),
            verticalArrangement   = Arrangement.spacedBy(0.dp),
        ) {
            item {
                HomeTopBar(
                    address         = state.deliveryAddress,
                    onAddressClick  = { showAddressSheet = true },
                    onNotifications = onNotifications,
                    onCart          = onCart,
                    onFavorites     = onFavorites,
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(RelaxDark, RelaxBackground), startY = 0f, endY = 60f))
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Card(
                        onClick   = onSearch,
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
                            Text(stringResource(R.string.home_search_placeholder), color = RelaxTextHint, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            if (bannerList.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        HorizontalPager(state = bannerPager, pageSpacing = 12.dp) { page ->
                            BannerCard(banner = bannerList[page], onClick = onPromotions)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            repeat(bannerList.size) { idx ->
                                val isSelected = idx == bannerPager.currentPage
                                val width by animateDpAsState(if (isSelected) 24.dp else 6.dp, label = "dot")
                                Box(
                                    modifier = Modifier
                                        .height(6.dp).width(width).padding(horizontal = 2.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isSelected) RelaxDark else RelaxDivider)
                                )
                            }
                        }
                    }
                }
            }

            if (state.categories.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        SectionHeader(title = stringResource(R.string.home_section_categories), modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.categories) { cat -> CategoryChip(category = cat, onClick = { onCategory(cat.id) }) }
                        }
                    }
                }
            }

            if (state.products.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        SectionHeader(title = stringResource(R.string.home_section_popular), onSeeAll = onSeeAllPopular, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(14.dp))
                        LazyHorizontalGrid(
                            rows                  = GridCells.Fixed(1),
                            modifier              = Modifier.height(280.dp),
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.products.take(6)) { product ->
                                ProductCardGrid(
                                    product          = product,
                                    onClick          = { onProduct(product.id) },
                                    quantity         = cartState.items.find { it.product.id == product.id }?.quantity ?: 0,
                                    onIncrease       = { cartViewModel.add(product) },
                                    onDecrease       = { cartViewModel.decrease(product.id) },
                                    isFavorite       = favoritesState.favorites.any { it.id == product.id },
                                    onToggleFavorite = { favoritesViewModel.toggle(product) },
                                    modifier         = Modifier.width(175.dp),
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(28.dp))
                    PromoStrip(onClick = onPromotions)
                }
            }

            if (state.newProducts.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        SectionHeader(title = stringResource(R.string.home_section_new), onSeeAll = onSeeAllNew, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.newProducts) { product ->
                                ProductCardHorizontal(product = product, onClick = { onProduct(product.id) }, onAddToCart = { cartViewModel.add(product) })
                            }
                        }
                    }
                }
            }

            if (state.saleProducts.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        SectionHeader(title = stringResource(R.string.home_section_sale_week), onSeeAll = onPromotions, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.saleProducts) { product ->
                                ProductCardHorizontal(product = product, onClick = { onProduct(product.id) }, onAddToCart = { cartViewModel.add(product) })
                            }
                        }
                    }
                }
            }

            if (state.products.size > 6) {
                item {
                    Spacer(Modifier.height(28.dp))
                    SectionHeader(title = stringResource(R.string.home_section_best_offers), onSeeAll = onSeeAllBestOffers, modifier = Modifier.padding(horizontal = 20.dp))
                    Spacer(Modifier.height(14.dp))
                }
                item {
                    LazyVerticalGrid(
                        columns               = GridCells.Fixed(2),
                        modifier              = Modifier.fillMaxWidth().heightIn(max = 800.dp).padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp),
                        userScrollEnabled     = false,
                    ) {
                        items(state.products.takeLast(6)) { product ->
                            ProductCardGrid(
                                product          = product,
                                onClick          = { onProduct(product.id) },
                                quantity         = cartState.items.find { it.product.id == product.id }?.quantity ?: 0,
                                onIncrease       = { cartViewModel.add(product) },
                                onDecrease       = { cartViewModel.decrease(product.id) },
                                isFavorite       = favoritesState.favorites.any { it.id == product.id },
                                onToggleFavorite = { favoritesViewModel.toggle(product) },
                            )
                        }
                    }
                }
            }
        }

        if (state.isLoading && state.products.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color    = RelaxDark,
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    address: String,
    onAddressClick: () -> Unit,
    onNotifications: () -> Unit,
    onCart: () -> Unit,
    onFavorites: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAddressClick)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Icon(Icons.Rounded.LocationOn, null, tint = RelaxOrange, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = address.ifBlank { stringResource(R.string.home_address_not_set) },
                        color    = if (address.isBlank()) RelaxTextOnDarkSub.copy(alpha = 0.55f) else RelaxTextOnDarkSub,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 180.dp),
                    )
                    Icon(Icons.Rounded.KeyboardArrowDown, null, tint = RelaxTextOnDarkSub, modifier = Modifier.size(14.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeIconButton(icon = Icons.Rounded.FavoriteBorder, badge = false, onClick = onFavorites)
                HomeIconButton(icon = Icons.Rounded.NotificationsNone, badge = true,  onClick = onNotifications)
                HomeIconButton(icon = Icons.Rounded.ShoppingCart,      badge = false, onClick = onCart)
            }
        }
    }
}

@Composable
private fun HomeIconButton(icon: ImageVector, badge: Boolean, onClick: () -> Unit) {
    Box {
        IconButton(
            onClick  = onClick,
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(RelaxWhite.copy(alpha = 0.12f))
        ) {
            Icon(icon, null, tint = RelaxWhite, modifier = Modifier.size(22.dp))
        }
        if (badge) {
            Box(modifier = Modifier.size(8.dp).align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp).background(RelaxRed, CircleShape))
        }
    }
}

@Composable
private fun BannerCard(banner: Banner, onClick: () -> Unit) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth().height(180.dp).shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(model = banner.imageUrl, contentDescription = banner.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            val bannerColor = parseHexColor(banner.backgroundColor, default = Color(0xFF1C1C1E))
            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = listOf(bannerColor.copy(alpha = 0.92f), bannerColor.copy(alpha = 0.4f)))))
            Column(modifier = Modifier.align(Alignment.CenterStart).padding(24.dp).fillMaxWidth(0.65f)) {
                if (!banner.badgeText.isNullOrEmpty()) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(RelaxWhite.copy(alpha = 0.22f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(banner.badgeText, color = RelaxWhite, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Text(banner.title, color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                Spacer(Modifier.height(6.dp))
                Text(banner.subtitle ?: "", color = RelaxWhite.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun CategoryChip(category: Category, onClick: () -> Unit) {
    Column(
        modifier            = Modifier.clip(RoundedCornerShape(16.dp)).background(parseHexColor(category.color)).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(category.icon ?: "", fontSize = 28.sp)
        Spacer(Modifier.height(6.dp))
        Text(category.name, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = RelaxTextPrimary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PromoStrip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE53935), Color(0xFFFF6B35))))
            .clickable(onClick = onClick).padding(24.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.home_promo_title), color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.home_promo_subtitle), color = RelaxWhite.copy(alpha = 0.88f), fontSize = 13.sp)
            }
            Icon(Icons.Rounded.ArrowForwardIos, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
        }
    }
}

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
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(RelaxRed, RelaxOrange))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.LocationOn, null, tint = RelaxWhite, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        stringResource(R.string.home_address_sheet_title),
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = RelaxTextPrimary,
                    )
                    Text(
                        stringResource(R.string.home_address_sheet_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = RelaxTextSecondary,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value         = address,
                onValueChange = { address = it },
                label         = { Text(stringResource(R.string.checkout_delivery_address_label)) },
                placeholder   = { Text(stringResource(R.string.checkout_address_placeholder), fontSize = 13.sp) },
                leadingIcon   = { Icon(Icons.Rounded.Home, null, tint = RelaxRed) },
                trailingIcon  = {
                    if (address.isNotEmpty()) {
                        IconButton(onClick = { address = "" }) {
                            Icon(Icons.Rounded.Close, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                modifier   = Modifier.fillMaxWidth(),
                shape      = RoundedCornerShape(14.dp),
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
                stringResource(R.string.home_address_hint),
                style  = MaterialTheme.typography.bodySmall,
                color  = RelaxTextHint,
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = { if (address.isNotBlank()) { onSave(address); onDismiss() } },
                enabled  = address.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
            ) {
                Icon(Icons.Rounded.Check, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.home_address_save),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                )
            }
        }
    }
}
