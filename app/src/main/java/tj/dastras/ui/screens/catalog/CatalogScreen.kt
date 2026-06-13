package tj.dastras.ui.screens.catalog

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import tj.dastras.R
import tj.dastras.ui.components.*
import tj.dastras.ui.screens.cart.CartViewModel
import tj.dastras.ui.screens.favorites.FavoritesViewModel
import tj.dastras.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onProduct: (Int) -> Unit,
    onCart: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = activityViewModel(),
    favoritesViewModel: FavoritesViewModel = activityViewModel(),
) {
    val state      = viewModel.uiState
    val cartState  = cartViewModel.uiState
    val favoritesState = favoritesViewModel.uiState
    val categories = listOf(Pair(0, stringResource(R.string.catalog_category_all))) + state.categories.map { Pair(it.id, it.name) }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.fillMaxWidth().background(RelaxWhite).statusBarsPadding()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.catalog_title), style = MaterialTheme.typography.headlineLarge, color = RelaxTextPrimary, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick  = onCart,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(RelaxSurfaceAlt)
                    ) {
                        Icon(Icons.Rounded.ShoppingCart, null, tint = RelaxTextPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(14.dp)).background(RelaxInputBg),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Search, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            if (state.searchQuery.isEmpty()) {
                                Text(stringResource(R.string.catalog_search_placeholder), color = RelaxTextHint, style = MaterialTheme.typography.bodyMedium)
                            }
                            BasicTextField(value = state.searchQuery, onValueChange = { viewModel.search(it) })
                        }
                    }
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                            .background(if (state.showFilter) RelaxDark else RelaxInputBg)
                            .clickable { viewModel.toggleFilter() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Tune, null, tint = if (state.showFilter) RelaxWhite else RelaxTextPrimary, modifier = Modifier.size(20.dp))
                    }
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(RelaxInputBg).clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Sort, null, tint = RelaxTextPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

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
                    Text(name, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) RelaxWhite else RelaxTextSecondary)
                }
            }
        }

        RelaxDivider()

        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.filteredProducts.isEmpty() -> EmptyState()
            else -> LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                modifier              = Modifier.fillMaxSize(),
                contentPadding        = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(stringResource(R.string.catalog_products_count, state.filteredProducts.size), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
                items(state.filteredProducts) { product ->
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

@Composable
private fun BasicTextField(value: String, onValueChange: (String) -> Unit) {
    androidx.compose.foundation.text.BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        singleLine    = true,
        modifier      = Modifier.fillMaxWidth(),
        textStyle     = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary),
    )
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.catalog_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
            Text(stringResource(R.string.catalog_empty_subtitle), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
        }
    }
}
