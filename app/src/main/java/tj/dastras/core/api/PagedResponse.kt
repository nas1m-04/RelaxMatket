package tj.dastras.core.api

data class PagedResponse<T>(
    val items: List<T>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean
)
