package tj.relax.core.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import tj.relax.core.db.dao.BannerDao
import tj.relax.core.db.dao.CachedProductDao
import tj.relax.core.db.dao.CategoryDao
import tj.relax.core.db.entity.BannerEntity
import tj.relax.core.db.entity.CachedProductEntity
import tj.relax.core.db.entity.CategoryEntity

@Database(
    entities = [
        CategoryEntity::class,
        BannerEntity::class,
        CachedProductEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun bannerDao(): BannerDao
    abstract fun cachedProductDao(): CachedProductDao
}

/** Room's KSP processor (room.generateKotlin=true) generates the real `initialize()` body for
 * each platform at compile time — these actuals just satisfy the expected shape. */
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
