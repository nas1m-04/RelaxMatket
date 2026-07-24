package tj.relax.core.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun buildAppDatabase(): AppDatabase =
    getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
