package tj.dastras.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import tj.dastras.data.MockData
import tj.dastras.ui.components.*
import tj.dastras.ui.theme.*

@Composable
fun FavoritesScreen(onBack: () -> Unit, onProduct: (Int) -> Unit) {
    val favorites = remember { MockData.products.take(5).toMutableStateList() }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = "Избранное", onBack = onBack)
        }

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❤️", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Пусто в избранном", style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                    Text("Добавляйте любимые товары", style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
            }
        } else {
            LazyVerticalGrid(
                columns             = GridCells.Fixed(2),
                contentPadding      = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text("${favorites.size} товаров", style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
                items(favorites) { product ->
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
