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
    viewModel { CartViewModel(get(), get(), get(), get()) }
    viewModel { CatalogViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get(), get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { LoyaltyViewModel(get(), get()) }
    viewModel { NotificationsViewModel(get()) }
    viewModel { OrderDetailViewModel(get(), get(), get()) }
    viewModel { OrdersViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { PromotionsViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { SupportViewModel(get()) }
}
