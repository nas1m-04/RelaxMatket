package tj.dastras.ui.screens.catalog.data.dto.request

// CatalogRequest.kt
data class CatalogRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
    val categoryId: Int? = null,
    val search: String? = null,
    val sort: String? = null,
)