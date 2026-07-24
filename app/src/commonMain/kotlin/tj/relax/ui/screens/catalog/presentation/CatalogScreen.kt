package tj.relax.ui.screens.catalog.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
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
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import tj.relax.generated.resources.*
import tj.relax.ui.components.*
import tj.relax.ui.screens.cart.CartViewModel
import tj.relax.ui.screens.catalog.ViewModel.CatalogViewModel
import tj.relax.ui.screens.favorites.FavoritesViewModel
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onProduct: (Int) -> Unit,
    onCart: () -> Unit,
    viewModel: CatalogViewModel = sharedViewModel(),
    cartViewModel: CartViewModel = sharedViewModel(),
    favoritesViewModel: FavoritesViewModel = sharedViewModel(),
) {
    val state          = viewModel.uiState
    val cartState      = cartViewModel.uiState
    val favoritesState = favoritesViewModel.uiState

    var showSortSheet   by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadIfNeeded() }

    val categories = listOf(Pair(0, stringResource(Res.string.catalog_category_all))) +
            state.categories.map { Pair(it.id, it.name) }

    val pullState = rememberPullToRefreshState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState) {
        snapshotFlow {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total       = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { viewModel.loadNextPage() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RelaxWhite)
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = stringResource(Res.string.catalog_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = RelaxTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    // Cart button — disabled, no delivery/pickup cart flow
                    /*
                    IconButton(
                        onClick  = onCart,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RelaxSurfaceAlt)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingCart,
                            contentDescription = null,
                            tint     = RelaxTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    */
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(RelaxInputBg),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector        = Icons.Rounded.Search,
                                contentDescription = null,
                                tint               = RelaxTextSecondary,
                                modifier           = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Box {
                                if (state.searchQuery.isEmpty()) {
                                    Text(
                                        text  = stringResource(Res.string.catalog_search_placeholder),
                                        color = RelaxTextHint,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                BasicTextField(
                                    value         = state.searchQuery,
                                    onValueChange = { viewModel.search(it) },
                                    singleLine    = true,
                                    modifier      = Modifier.fillMaxWidth(),
                                    textStyle     = MaterialTheme.typography.bodyMedium
                                        .copy(color = RelaxTextPrimary),
                                )
                            }
                        }
                    }

                    // Кнопка фильтра
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (state.showFilter) RelaxDark else RelaxInputBg)
                            .clickable {
                                showFilterSheet = true
                                viewModel.toggleFilter()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.Tune,
                            contentDescription = null,
                            tint     = if (state.showFilter) RelaxWhite else RelaxTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Кнопка сортировки
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (state.sortBy != null) RelaxDark else RelaxInputBg)
                            .clickable { showSortSheet = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.Sort,
                            contentDescription = null,
                            tint     = if (state.sortBy != null) RelaxWhite else RelaxTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ── Категории ────────────────────────────────────────────────────────
        LazyRow(
            modifier              = Modifier.background(RelaxWhite),
            contentPadding        = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories) { (id, name) ->
                val isSelected = state.selectedCategoryId == id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) RelaxDark else RelaxInputBg)
                        .clickable { viewModel.selectCategory(id) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text       = name,
                        fontSize   = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) RelaxWhite else RelaxTextSecondary
                    )
                }
            }
        }

        // ── Активный фильтр "Новинки" ─────────────────────────────────────
        AnimatedVisibility(visible = state.showNewOnly) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RelaxWhite)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(RelaxDark)
                        .clickable { viewModel.setNewOnly(false) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text       = stringResource(Res.string.home_section_new),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = RelaxWhite
                    )
                    Icon(
                        imageVector        = Icons.Rounded.Close,
                        contentDescription = null,
                        tint     = RelaxWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        RelaxDivider()

        // ── Контент ───────────────────────────────────────────────────────────
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh    = { viewModel.refresh() },
            state        = pullState,
            modifier     = Modifier.fillMaxSize().weight(1f),
        ) {
            when {
                state.isLoading -> {
                    CatalogScreenSkeleton()
                }

                state.products.isEmpty() -> {
                    EmptyState()
                }

                else -> {
                    LazyVerticalGrid(
                        columns               = GridCells.Fixed(2),
                        state                 = gridState,
                        modifier              = Modifier.fillMaxSize(),
                        contentPadding        = PaddingValues(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp),
                    ) {
                        // Счётчик товаров
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text  = stringResource(Res.string.catalog_products_count, state.products.size),
                                style = MaterialTheme.typography.bodyMedium,
                                color = RelaxTextSecondary
                            )
                        }

                        // Товары
                        items(
                            items = state.products,
                            key   = { it.id }
                        ) { product ->
                            ProductCardGrid(
                                product          = product,
                                onClick          = { onProduct(product.id) },
                                quantity         = cartState.items
                                    .find { it.product.id == product.id }?.quantity ?: 0,
                                onIncrease       = { cartViewModel.add(product) },
                                onDecrease       = { cartViewModel.decrease(product.id) },
                                isFavorite       = favoritesState.favorites.any { it.id == product.id },
                                onToggleFavorite = { favoritesViewModel.toggle(product) },
                            )
                        }

                        // Спиннер при загрузке следующей страницы
                        if (state.isLoadingMore) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier         = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color    = RelaxDark,
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        CatalogFilterSheet(
            state     = state,
            onDismiss = { showFilterSheet = false; viewModel.toggleFilter() },
            onApply   = { newOnly, from, to ->
                viewModel.setNewOnly(newOnly)
                viewModel.applyPriceFilter(from, to)
            }
        )
    }

    if (showSortSheet) {
        CatalogSortSheet(
            current   = state.sortBy,
            onDismiss = { showSortSheet = false },
            onSelect  = { viewModel.sort(it) }
        )
    }


}

// ── Вспомогательные composable ────────────────────────────────────────────────

@Composable
private fun EmptyState() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text  = stringResource(Res.string.catalog_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                color = RelaxTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = stringResource(Res.string.catalog_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = RelaxTextSecondary
            )
        }
    }
}