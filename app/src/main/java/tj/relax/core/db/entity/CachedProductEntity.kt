package tj.relax.core.db.entity

import androidx.room.Entity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.relax.data.Product

@Entity(tableName = "cached_products", primaryKeys = ["id", "listType"])
data class CachedProductEntity(
    val id: Int,
    val listType: String,       // "popular" | "new" | "sale"
    val name: String,
    val brand: String?,
    val imageUrl: String?,
    val imagesJson: String,     // JSON array
    val price: Double,
    val oldPrice: Double?,
    val cardPrice: Double?,
    val unit: String?,
    val weight: String?,
    val rating: Double,
    val reviewCount: Int,
    val categoryId: Int,
    val isNew: Boolean,
    val description: String?,
    val composition: String?,
    val inStock: Boolean,
)

private val gson = Gson()
private val stringListType = object : TypeToken<List<String>>() {}.type

fun CachedProductEntity.toDomain() = Product(
    id           = id,
    name         = name,
    brand        = brand,
    imageUrl     = imageUrl,
    images       = try { gson.fromJson(imagesJson, stringListType) } catch (_: Exception) { emptyList() },
    price        = price,
    oldPrice     = oldPrice,
    cardPrice    = cardPrice,
    unit         = unit,
    weight       = weight,
    rating       = rating,
    reviewCount  = reviewCount,
    categoryId   = categoryId,
    isNew        = isNew,
    description  = description,
    composition  = composition,
    inStock      = inStock,
    isFavorite   = false,
)

fun Product.toEntity(listType: String) = CachedProductEntity(
    id          = id,
    listType    = listType,
    name        = name,
    brand       = brand,
    imageUrl    = imageUrl,
    imagesJson  = gson.toJson(images),
    price       = price,
    oldPrice    = oldPrice,
    cardPrice   = cardPrice,
    unit        = unit,
    weight      = weight,
    rating      = rating,
    reviewCount = reviewCount,
    categoryId  = categoryId,
    isNew       = isNew,
    description = description,
    composition = composition,
    inStock     = inStock,
)
