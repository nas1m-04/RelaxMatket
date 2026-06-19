package tj.dastras.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import tj.dastras.ui.screens.splash.SplashViewModel
import tj.dastras.ui.screens.auth.LoginScreen
import tj.dastras.ui.screens.auth.RegisterScreen
import tj.dastras.ui.screens.branch.SelectBranchScreen
import tj.dastras.ui.screens.cart.CartScreen
import tj.dastras.ui.screens.checkout.CheckoutScreen
import tj.dastras.ui.screens.favorites.FavoritesScreen
import tj.dastras.ui.screens.main.MainScreen
import tj.dastras.ui.screens.notifications.NotificationsScreen
import tj.dastras.ui.screens.onboarding.OnboardingScreen
import tj.dastras.ui.screens.orders.OrderHistoryScreen
import tj.dastras.ui.screens.product.ProductDetailScreen
import tj.dastras.ui.screens.promotions.PromotionsScreen
import tj.dastras.ui.screens.search.SearchScreen
import tj.dastras.ui.screens.splash.SplashScreen
import tj.dastras.ui.components.ErrorAlertDialogHost

@Composable
fun RelaxNavGraph(navController: NavHostController) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
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
            val splashViewModel: SplashViewModel = hiltViewModel()
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

        composable(Route.Promotions.route) {
            PromotionsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Favorites.route) {
            FavoritesScreen(
                onBack = { navController.popBackStack() },
                onProduct = { id -> navController.navigate(Route.ProductDetail.createRoute(id)) }
            )
        }

        composable(Route.OrderHistory.route) {
            OrderHistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Search.route) {
            SearchScreen(
                onBack    = { navController.popBackStack() },
                onProduct = { id -> navController.navigate(Route.ProductDetail.createRoute(id)) },
            )
        }
    }

    ErrorAlertDialogHost()
}
