package tj.relax.ui.screens.catalog.data

import tj.relax.data.Product
import tj.relax.ui.screens.catalog.data.dto.response.ProductResponse


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