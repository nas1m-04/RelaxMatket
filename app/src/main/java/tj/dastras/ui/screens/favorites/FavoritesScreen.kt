package tj.dastras.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import tj.dastras.R
import tj.dastras.ui.components.*
import tj.dastras.ui.screens.cart.CartViewModel
import tj.dastras.ui.theme.*

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onProduct: (Int) -> Unit,
    viewModel: FavoritesViewModel = activityViewModel(),
    cartViewModel: CartViewModel = activityViewModel(),
) {
    val state     = viewModel.uiState
    val cartState = cartViewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(R.string.menu_favorites), onBack = onBack)
        }

        when {
            state.isLoading && state.favorites.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.favorites.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.favorites_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                    Text(stringResource(R.string.favorites_empty_subtitle), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
            }
            else -> LazyVerticalGrid(
                columns               = GridCells.Fixed(2),
                contentPadding        = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(stringResource(R.string.favorites_count, state.favorites.size), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
                items(state.favorites) { product ->
                    ProductCardGrid(
                        product          = product,
                        onClick          = { onProduct(product.id) },
                        quantity         = cartState.items.find { it.product.id == product.id }?.quantity ?: 0,
                        onIncrease       = { cartViewModel.add(product) },
                        onDecrease       = { cartViewModel.decrease(product.id) },
                        isFavorite       = true,
                        onToggleFavorite = { viewModel.toggle(product) },
                    )
                }
            }
        }
    }
}
