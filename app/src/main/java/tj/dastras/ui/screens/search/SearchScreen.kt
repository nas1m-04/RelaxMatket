package tj.dastras.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tj.dastras.R
import tj.dastras.ui.components.*
import tj.dastras.ui.screens.cart.CartViewModel
import tj.dastras.ui.screens.favorites.FavoritesViewModel
import tj.dastras.ui.theme.*

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onProduct: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = activityViewModel(),
    favoritesViewModel: FavoritesViewModel = activityViewModel(),
) {
    val state          = viewModel.uiState
    val cartState      = cartViewModel.uiState
    val favoritesState = favoritesViewModel.uiState
    val focusRequester = remember { FocusRequester() }
    val keyboard       = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.fillMaxWidth().background(RelaxWhite).statusBarsPadding()) {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary, modifier = Modifier.size(18.dp))
                }
                Box(
                    modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(14.dp)).background(RelaxInputBg),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Search, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (state.query.isEmpty()) {
                                Text(stringResource(R.string.home_search_placeholder), color = RelaxTextHint, style = MaterialTheme.typography.bodyMedium)
                            }
                            BasicTextField(
                                value         = state.query,
                                onValueChange = { viewModel.setQuery(it) },
                                singleLine    = true,
                                modifier      = Modifier.fillMaxWidth().focusRequester(focusRequester),
                                textStyle     = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary),
                            )
                        }
                        if (state.query.isNotEmpty()) {
                            Icon(
                                Icons.Rounded.Close, null,
                                tint     = RelaxTextSecondary,
                                modifier = Modifier.size(18.dp).clickable { viewModel.setQuery("") },
                            )
                        }
                    }
                }
            }
        }
        RelaxDivider()

        when {
            state.query.isBlank() -> SearchMessage(
                title    = stringResource(R.string.search_hint_title),
                subtitle = stringResource(R.string.search_hint_subtitle),
            )
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.results.isEmpty() && state.hasSearched -> SearchMessage(
                title    = stringResource(R.string.catalog_empty_title),
                subtitle = stringResource(R.string.search_empty_subtitle),
            )
            else -> LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                modifier              = Modifier.fillMaxSize(),
                contentPadding        = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(stringResource(R.string.catalog_products_count, state.results.size), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
                items(state.results) { product ->
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
private fun SearchMessage(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
        }
    }
}
