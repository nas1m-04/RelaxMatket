package tj.relax.core.di

import org.koin.dsl.module
import tj.relax.data.CategoryRepository
import tj.relax.data.ProductRepository
import tj.relax.ui.screens.home.data.repository.BannerRepository

/** Repositories still stuck on androidMain because they depend on Room DAOs, and Room hasn't
 * moved to commonMain yet (Phase 9) — everything else lives in the portable [appModule]. */
val androidAppModule = module {
    single { CategoryRepository(get(), get(), get()) }
    single { ProductRepository(get(), get(), get()) }
    single { BannerRepository(get(), get(), get()) }
}
