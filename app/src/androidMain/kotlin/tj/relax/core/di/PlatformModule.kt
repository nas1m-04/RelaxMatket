package tj.relax.core.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import tj.relax.core.db.AppDatabase
import tj.relax.data.LocalUserStore
import tj.relax.data.LoyaltyLocalStore
import tj.relax.data.TokenManager

actual fun platformModule(): Module = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "relax_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().bannerDao() }
    single { get<AppDatabase>().cachedProductDao() }

    single { TokenManager(androidContext()) }
    single { LocalUserStore(androidContext()) }
    single { LoyaltyLocalStore(androidContext()) }
}
