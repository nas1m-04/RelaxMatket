package tj.relax.ui.screens.catalog.data.dto.request

import kotlinx.serialization.Serializable

// CatalogRequest.kt
@Serializable
data class CatalogRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
    val categoryId: Int? = null,
    val search: String? = null,
    val sort: String? = null,
    val priceFrom: Int? = null,
    val priceTo: Int? = null,
)