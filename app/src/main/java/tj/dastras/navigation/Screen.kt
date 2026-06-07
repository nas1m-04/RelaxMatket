package tj.dastras.navigation

sealed class Screen(val route: String) {
    object Splash        : Screen("splash")
    object Onboarding    : Screen("onboarding")
    object Login         : Screen("login")
    object Otp           : Screen("otp/{phone}") {
        fun createRoute(phone: String) = "otp/$phone"
    }
    object Main          : Screen("main")
    object Home          : Screen("home")
    object Catalog       : Screen("catalog")
    object LoyaltyCard   : Screen("loyalty_card")
    object Bonuses       : Screen("bonuses")
    object Profile       : Screen("profile")
    object ProductDetail : Screen("product/{id}") {
        fun createRoute(id: Int) = "product/$id"
    }
    object Cart          : Screen("cart")
    object Checkout      : Screen("checkout")
    object Promotions    : Screen("promotions")
    object Favorites     : Screen("favorites")
    object OrderHistory  : Screen("order_history")
    object Notifications : Screen("notifications")
}
