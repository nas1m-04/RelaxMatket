package tj.relax.core.db

import androidx.room.Room
import androidx.room.RoomDatabase
import tj.relax.core.util.AndroidPlatformContext

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = AndroidPlatformContext.applicationContext
    val dbFile = context.getDatabasePath("relax_db")
    return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
        .fallbackToDestructiveMigration(dropAllTables = true)
}
