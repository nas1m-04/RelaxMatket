package tj.relax.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tj.relax.core.db.entity.CachedProductEntity

@Dao
interface CachedProductDao {
    @Query("SELECT * FROM cached_products WHERE listType = :type")
    suspend fun getByType(type: String): List<CachedProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CachedProductEntity>)

    @Query("DELETE FROM cached_products WHERE listType = :type")
    suspend fun deleteByType(type: String)
}
