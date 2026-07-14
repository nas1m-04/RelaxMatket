package tj.relax.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tj.relax.core.db.entity.BannerEntity

@Dao
interface BannerDao {
    @Query("SELECT * FROM banners ORDER BY id")
    suspend fun getAll(): List<BannerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BannerEntity>)

    @Query("DELETE FROM banners")
    suspend fun deleteAll()
}
