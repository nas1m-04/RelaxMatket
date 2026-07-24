package tj.relax.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import tj.relax.data.Banner

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val subtitle: String?,
    val imageUrl: String?,
    val backgroundColor: String?,
    val badgeText: String?,
)

fun BannerEntity.toDomain() = Banner(id, title, subtitle, imageUrl, backgroundColor, badgeText)
fun Banner.toEntity() = BannerEntity(id, title, subtitle, imageUrl, backgroundColor, badgeText)
