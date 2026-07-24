package tj.relax.core.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import tj.relax.data.LocalUserStore
import tj.relax.data.LoyaltyLocalStore
import tj.relax.data.TokenManager

actual fun platformModule(): Module = module {
    single<Settings>(named("auth")) {
        NSUserDefaultsSettings(NSUserDefaults(suiteName = "auth_prefs"))
    }
    single<Settings>(named("user")) {
        NSUserDefaultsSettings(NSUserDefaults(suiteName = "user_prefs"))
    }
    single<Settings>(named("loyalty")) {
        NSUserDefaultsSettings(NSUserDefaults(suiteName = "loyalty_prefs"))
    }

    single { TokenManager(get(named("auth"))) }
    single { LocalUserStore(get(named("user"))) }
    single { LoyaltyLocalStore(get(named("loyalty"))) }
}
