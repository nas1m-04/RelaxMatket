package tj.relax.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import tj.relax.ui.screens.splash.SplashViewModel
import tj.relax.ui.screens.auth.LoginScreen
import tj.relax.ui.screens.auth.RegisterScreen
import tj.relax.ui.screens.branch.SelectBranchScreen
// Cart/checkout flow disabled — no delivery/pickup, see composable(Route.Cart...) below
// import tj.relax.ui.screens.cart.CartScreen
// import tj.relax.ui.screens.checkout.CheckoutScreen
import tj.relax.ui.screens.favorites.FavoritesScreen
import tj.relax.ui.screens.loyaltycard.LoyaltyCardDetailScreen
import tj.relax.ui.screens.main.MainScreen
import tj.relax.ui.screens.notifications.NotificationsScreen
import tj.relax.ui.screens.onboarding.OnboardingScreen
import tj.relax.ui.screens.orders.OrderDetailScreen
import tj.relax.ui.screens.product.ProductDetailScreen
import tj.relax.ui.screens.promotions.PromotionsScreen
import tj.relax.ui.screens.search.SearchScreen
import tj.relax.ui.screens.splash.SplashScreen
import tj.relax.ui.screens.support.SupportScreen
import tj.relax.ui.screens.about.AboutScreen
import tj.relax.ui.components.ErrorAlertDialogHost

@Composable
fun RelaxNavGraph(navController: NavHostController) {
    val sessionViewModel: SessionViewModel = koinViewModel()
    LaunchedEffect(Unit) {
        sessionViewModel.sessionExpired.collect {
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController    = navController,
        startDestination = Route.Splash.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(340, easing = EaseOutCubic),
            ) + fadeIn(tween(280, easing = LinearOutSlowInEasing))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300, easing = EaseInCubic),
            ) + fadeOut(tween(220))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(340, easing = EaseOutCubic),
            ) + fadeIn(tween(280))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300, easing = EaseInCubic),
            ) + fadeOut(tween(220))
        },
    ) {
        composable(
            Route.Splash.route,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition  = { fadeOut(tween(450)) },
        ) {
            val splashViewModel: SplashViewModel = koinViewModel()
            SplashScreen(onFinished = {
                val destination = if (splashViewModel.isLoggedIn) Route.Main.route else Route.Onboarding.route
                navController.navigate(destination) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Route.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Route.Login.route) {
                    popUpTo(Route.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(
            Route.Login.route,
            enterTransition = { fadeIn(tween(380, easing = EaseOut)) + scaleIn(tween(380, easing = EaseOut), initialScale = 0.97f) },
        ) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Route.Register.route)
                },
                onNavigateToMain = {
                    navController.navigate(Route.Main.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onNavigateToMain = {
                    navController.navigate(Route.SelectBranch.createRoute("onboarding")) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Route.SelectBranch.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStack ->
            val mode = backStack.arguments?.getString("mode") ?: "settings"
            val isOnboarding = mode == "onboarding"
            SelectBranchScreen(
                isOnboarding = isOnboarding,
                onBack = { navController.popBackStack() },
                onDone = {
                    if (isOnboarding) {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.SelectBranch.createRoute(mode)) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            Route.Main.route,
            enterTransition    = { fadeIn(tween(400, easing = EaseOut)) + scaleIn(tween(400, easing = EaseOut), initialScale = 0.95f) },
            exitTransition     = { fadeOut(tween(280)) + scaleOut(tween(280), targetScale = 0.97f) },
            popEnterTransition = { fadeIn(tween(380, easing = EaseOut)) + scaleIn(tween(380, easing = EaseOut), initialScale = 0.97f) },
            popExitTransition  = { fadeOut(tween(280)) + scaleOut(tween(280), targetScale = 0.95f) },
        ) {
            MainScreen(
                rootNavController = navController,
                onLoggedOut = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Route.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStack ->
            ProductDetailScreen(
                productId = backStack.arguments?.getInt("id") ?: 1,
                onBack    = { navController.popBackStack() },
            )
        }

        composable(
            route     = Route.OrderDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            OrderDetailScreen(onBack = { navController.popBackStack() })
        }

        // Cart/checkout flow disabled — no delivery/pickup
        /*
        composable(Route.Cart.route) {
            CartScreen(
                onBack     = { navController.popBackStack() },
                onCheckout = { navController.navigate(Route.Checkout.route) }
            )
        }

        composable(Route.Checkout.route) {
            CheckoutScreen(
                onBack    = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Route.Main.route) {
                        popUpTo(Route.Cart.route) { inclusive = true }
                    }
                }
            )
        }
        */

        composable(Route.Promotions.route) {
            PromotionsScreen(
                onBack    = { navController.popBackStack() },
                onProduct = { id -> navController.navigate(Route.ProductDetail.createRoute(id)) },
            )
        }

        composable(Route.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.LoyaltyCardDetail.route) {
            LoyaltyCardDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Favorites.route) {
            FavoritesScreen(
                onBack = { navController.popBackStack() },
                onProduct = { id -> navController.navigate(Route.ProductDetail.createRoute(id)) }
            )
        }
        composable(Route.Search.route) {
            SearchScreen(
                onBack    = { navController.popBackStack() },
                onProduct = { id -> navController.navigate(Route.ProductDetail.createRoute(id)) },
            )
        }

        composable(Route.Support.route) {
            SupportScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }

    ErrorAlertDialogHost()
}
