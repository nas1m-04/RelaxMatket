package tj.dastras.navigation

import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Splash        : Screen("splash")
    object Onboarding    : Screen("onboarding")
    object Login         : Screen("login")
    object Otp           : Screen("otp/{phone}/{verificationId}") {
        fun createRoute(phone: String, verificationId: String): String {
            val encodedPhone = URLEncoder.encode(phone, "UTF-8")
            val encodedId    = URLEncoder.encode(verificationId, "UTF-8")
            return "otp/$encodedPhone/$encodedId"
        }
        fun decodeArg(value: String): String = URLDecoder.decode(value, "UTF-8")
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
