package tj.relax.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import tj.relax.data.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val icon: String?,
    val color: String?,
    val productCount: Int,
)

fun CategoryEntity.toDomain() = Category(id, name, icon, color, productCount)
fun Category.toEntity() = CategoryEntity(id, name, icon, color, productCount)
