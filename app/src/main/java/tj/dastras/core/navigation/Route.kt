package tj.dastras.core.navigation

sealed class Route(val route: String) {
    object Splash        : Route("splash")
    object Onboarding    : Route("onboarding")
    object Login         : Route("login")
    object Register      : Route("register")
    object Main          : Route("main")
    object Home          : Route("home")
    object Catalog       : Route("catalog")
    object Search        : Route("search")
    object LoyaltyCard   : Route("loyalty_card")
    object Bonuses       : Route("bonuses")
    object Profile       : Route("profile")
    object ProductDetail : Route("product/{id}") {
        fun createRoute(id: Int) = "product/$id"
    }
    object Cart          : Route("cart")
    object Checkout      : Route("checkout")
    object Promotions    : Route("promotions")
    object Favorites     : Route("favorites")
    object OrderHistory  : Route("order_history")
    object Notifications : Route("notifications")
    object SelectBranch  : Route("select_branch/{mode}") {
        fun createRoute(mode: String) = "select_branch/$mode"
    }
}
