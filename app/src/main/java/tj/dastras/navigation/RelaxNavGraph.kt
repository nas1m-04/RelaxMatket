package tj.dastras.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import tj.dastras.ui.screens.auth.LoginScreen
import tj.dastras.ui.screens.auth.OtpScreen
import tj.dastras.ui.screens.bonuses.BonusesScreen
import tj.dastras.ui.screens.cart.CartScreen
import tj.dastras.ui.screens.catalog.CatalogScreen
import tj.dastras.ui.screens.checkout.CheckoutScreen
import tj.dastras.ui.screens.favorites.FavoritesScreen
import tj.dastras.ui.screens.home.HomeScreen
import tj.dastras.ui.screens.loyaltycard.LoyaltyCardScreen
import tj.dastras.ui.screens.main.MainScreen
import tj.dastras.ui.screens.notifications.NotificationsScreen
import tj.dastras.ui.screens.onboarding.OnboardingScreen
import tj.dastras.ui.screens.orders.OrderHistoryScreen
import tj.dastras.ui.screens.product.ProductDetailScreen
import tj.dastras.ui.screens.profile.ProfileScreen
import tj.dastras.ui.screens.promotions.PromotionsScreen
import tj.dastras.ui.screens.splash.SplashScreen

@Composable
fun RelaxNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToOtp  = { phone -> navController.navigate(Screen.Otp.createRoute(phone)) },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Screen.Otp.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStack ->
            OtpScreen(
                phone    = backStack.arguments?.getString("phone") ?: "",
                onVerified = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack   = { navController.popBackStack() }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }

        composable(
            route     = Screen.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStack ->
            ProductDetailScreen(
                productId = backStack.arguments?.getInt("id") ?: 1,
                onBack    = { navController.popBackStack() },
                onCart    = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onBack     = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onBack    = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Promotions.route) {
            PromotionsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBack = { navController.popBackStack() },
                onProduct = { id -> navController.navigate(Screen.ProductDetail.createRoute(id)) }
            )
        }

        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(onBack = { navController.popBackStack() })
        }
    }
}
