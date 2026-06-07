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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import tj.dastras.data.MockData
import tj.dastras.ui.components.*
import tj.dastras.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onProduct: (Int) -> Unit, onCart: () -> Unit) {
    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(0) } // 0 = all
    var showFilter       by remember { mutableStateOf(false) }
    var sortBy           by remember { mutableStateOf("popular") }

    val categories  = listOf(Pair(0, "Все")) + MockData.categories.map { Pair(it.id, it.name) }
    val allProducts = MockData.products.filter { product ->
        val matchCat   = selectedCategory == 0 || product.categoryId == selectedCategory
        val matchQuery = searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true)
        matchCat && matchQuery
    }.let { list ->
        when (sortBy) {
            "price_asc"  -> list.sortedBy { it.price }
            "price_desc" -> list.sortedByDescending { it.price }
            "rating"     -> list.sortedByDescending { it.rating }
            else         -> list
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RelaxWhite)
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Каталог", style = MaterialTheme.typography.headlineLarge, color = RelaxTextPrimary, modifier = Modifier.weight(1f))
                    // Cart button
                    IconButton(
                        onClick  = onCart,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RelaxSurfaceAlt)
                    ) {
                        Icon(Icons.Rounded.ShoppingCart, null, tint = RelaxTextPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Search + Filter row
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(RelaxInputBg),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Search, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            if (searchQuery.isEmpty()) {
                                Text("Поиск в каталоге...", color = RelaxTextHint, style = MaterialTheme.typography.bodyMedium)
                            }
                            BasicTextField(value = searchQuery, onValueChange = { searchQuery = it })
                        }
                    }
                    // Filter button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (showFilter) RelaxDark else RelaxInputBg)
                            .clickable { showFilter = !showFilter },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Rounded.Tune,
                            null,
                            tint     = if (showFilter) RelaxWhite else RelaxTextPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    // Sort button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(RelaxInputBg)
                            .clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Sort, null, tint = RelaxTextPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Category tabs
        LazyRow(
            modifier            = Modifier.background(RelaxWhite),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories) { (id, name) ->
                val isSelected = selectedCategory == id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) RelaxDark else RelaxInputBg)
                        .clickable { selectedCategory = id }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text       = name,
                        fontSize   = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) RelaxWhite else RelaxTextSecondary,
                    )
                }
            }
        }

        RelaxDivider()

        // Results info + grid
        if (allProducts.isEmpty()) {
            EmptyState()
        } else {
            LazyVerticalGrid(
                columns             = GridCells.Fixed(2),
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${allProducts.size} товаров",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RelaxTextSecondary,
                        )
                    }
                }
                items(allProducts) { product ->
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

@Composable
private fun BasicTextField(value: String, onValueChange: (String) -> Unit) {
    androidx.compose.foundation.text.BasicTextField(
        value          = value,
        onValueChange  = onValueChange,
        singleLine     = true,
        modifier       = Modifier.fillMaxWidth(),
        textStyle      = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary),
    )
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text("Ничего не найдено", style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
            Text("Попробуйте изменить фильтры", style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
        }
    }
}
