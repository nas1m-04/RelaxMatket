package tj.relax.core.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import tj.relax.core.navigation.SessionViewModel
import tj.relax.ui.screens.auth.AuthViewModel
import tj.relax.ui.screens.branch.SelectBranchViewModel
import tj.relax.ui.screens.cart.CartViewModel
import tj.relax.ui.screens.catalog.ViewModel.CatalogViewModel
import tj.relax.ui.screens.checkout.CheckoutViewModel
import tj.relax.ui.screens.favorites.FavoritesViewModel
import tj.relax.ui.screens.home.ViewModel.HomeViewModel
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.LoyaltyViewModel
import tj.relax.ui.screens.notifications.NotificationsViewModel
import tj.relax.ui.screens.orders.OrderDetailViewModel
import tj.relax.ui.screens.orders.OrdersViewModel
import tj.relax.ui.screens.product.ProductDetailViewModel
import tj.relax.ui.screens.profile.ProfileViewModel
import tj.relax.ui.screens.promotions.PromotionsViewModel
import tj.relax.ui.screens.search.SearchViewModel
import tj.relax.ui.screens.splash.SplashViewModel
import tj.relax.ui.screens.support.SupportViewModel

val viewModelModule = module {
    viewModel { SessionViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { SelectBranchViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { NotificationsViewModel(get()) }
    viewModel { OrderDetailViewModel(get(), get(), get()) }
    viewModel { OrdersViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { SupportViewModel(get()) }

    // Shared across many screens (not per-screen instances) — retrieved via
    // tj.relax.ui.components.sharedViewModel() instead of koinViewModel(). A Koin `single`
    // gives them the same app-lifetime, single-instance semantics the old Activity-scoped
    // hiltViewModel(activity)/koinViewModel(viewModelStoreOwner = activity) pattern did,
    // without needing a ComponentActivity (no Compose Multiplatform equivalent on iOS).
    single { CartViewModel(get(), get(), get(), get()) }
    single { CatalogViewModel(get(), get()) }
    single { CheckoutViewModel(get(), get(), get(), get(), get()) }
    single { FavoritesViewModel(get(), get()) }
    single { LoyaltyViewModel(get(), get()) }
    single { PromotionsViewModel(get(), get()) }
}
