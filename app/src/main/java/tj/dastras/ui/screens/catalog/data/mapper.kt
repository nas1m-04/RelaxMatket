package tj.dastras.ui.screens.catalog.data

import tj.dastras.data.Product
import tj.dastras.ui.screens.catalog.data.dto.response.ProductResponse


fun ProductResponse.toDomain() = Product(
    id       = id,
    name     = name,
    price    = price,
    oldPrice = oldPrice,
    imageUrl = imageUrl,
    categoryId = categoryId,
    rating   = rating.toDouble(),
    isNew    = isNew,
)