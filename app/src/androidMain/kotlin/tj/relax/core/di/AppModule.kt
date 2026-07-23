package tj.relax.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module
import tj.relax.data.AuthRepository
import tj.relax.data.BranchRepository
import tj.relax.data.CartRepository
import tj.relax.data.CategoryRepository
import tj.relax.data.FavoritesRepository
import tj.relax.data.LoyaltyRepository
import tj.relax.data.NotificationsRepository
import tj.relax.data.OrderRepository
import tj.relax.data.ProductRepository
import tj.relax.data.PromotionsRepository
import tj.relax.data.SessionManager
import tj.relax.data.SupportRepository
import tj.relax.data.UserRepository
import tj.relax.ui.screens.home.data.repository.BannerRepository

val appModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    single { SessionManager() }

    single { AuthRepository(get(), get(), get()) }
    single { BranchRepository(get()) }
    single { CartRepository(get()) }
    single { CategoryRepository(get(), get(), get()) }
    single { FavoritesRepository(get()) }
    single { LoyaltyRepository(get(), get()) }
    single { NotificationsRepository(get()) }
    single { OrderRepository(get()) }
    single { ProductRepository(get(), get(), get()) }
    single { PromotionsRepository(get()) }
    single { SupportRepository(get()) }
    single { UserRepository(get(), get()) }
    single { BannerRepository(get(), get(), get()) }
}
