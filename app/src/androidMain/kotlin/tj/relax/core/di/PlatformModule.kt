package tj.relax.core.di

import android.content.Context
import androidx.room.Room
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
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

    single<Settings>(named("auth")) {
        SharedPreferencesSettings(androidContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE))
    }
    single<Settings>(named("user")) {
        SharedPreferencesSettings(androidContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
    }
    single<Settings>(named("loyalty")) {
        SharedPreferencesSettings(androidContext().getSharedPreferences("loyalty_prefs", Context.MODE_PRIVATE))
    }

    single { TokenManager(get(named("auth"))) }
    single { LocalUserStore(get(named("user"))) }
    single { LoyaltyLocalStore(get(named("loyalty"))) }
}
