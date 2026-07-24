package tj.relax.core.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = NSHomeDirectory() + "/relax_db"
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
        .fallbackToDestructiveMigration(dropAllTables = true)
}
