package tj.dastras.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import tj.dastras.R
import tj.dastras.navigation.Screen
import tj.dastras.ui.components.activityViewModel
import tj.dastras.ui.screens.bonuses.BonusesScreen
import tj.dastras.ui.screens.catalog.CatalogScreen
import tj.dastras.ui.screens.catalog.CatalogViewModel
import tj.dastras.ui.screens.home.HomeScreen
import tj.dastras.ui.screens.loyaltycard.LoyaltyCardScreen
import tj.dastras.ui.screens.profile.ProfileScreen
import tj.dastras.ui.theme.*

private data class NavItem(
    val route: String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val labelRes: Int,
    val isCenter: Boolean = false,
)

private val navItems = listOf(
    NavItem(Screen.Home.route,        Icons.Rounded.Home,           Icons.Rounded.Home,           R.string.nav_home),
    NavItem(Screen.Catalog.route,     Icons.Rounded.GridView,       Icons.Rounded.GridView,       R.string.nav_catalog),
    NavItem(Screen.LoyaltyCard.route, Icons.Rounded.CreditCard,     Icons.Rounded.CreditCard,     R.string.nav_card, true),
    NavItem(Screen.Bonuses.route,     Icons.Rounded.Stars,          Icons.Rounded.Stars,          R.string.nav_bonuses),
    NavItem(Screen.Profile.route,     Icons.Rounded.PersonOutline,  Icons.Rounded.Person,         R.string.nav_profile),
)

@Composable
fun MainScreen(rootNavController: NavHostController, onLoggedOut: () -> Unit) {
    val bottomNavController = rememberNavController()
    val backStack           by bottomNavController.currentBackStackEntryAsState()
    val currentRoute        = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            RelaxBottomNavBar(
                currentRoute      = currentRoute,
                onNavigate        = { route ->
                    bottomNavController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        },
        containerColor = RelaxBackground,
    ) { innerPadding ->
        NavHost(
            navController    = bottomNavController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                val catalogViewModel: CatalogViewModel = activityViewModel()
                val goToCatalog: () -> Unit = {
                    bottomNavController.navigate(Screen.Catalog.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
                HomeScreen(
                    onProduct     = { id -> rootNavController.navigate(Screen.ProductDetail.createRoute(id)) },
                    onCart        = { rootNavController.navigate(Screen.Cart.route) },
                    onNotifications = { rootNavController.navigate(Screen.Notifications.route) },
                    onPromotions  = { rootNavController.navigate(Screen.Promotions.route) },
                    onFavorites   = { rootNavController.navigate(Screen.Favorites.route) },
                    onSearch      = { rootNavController.navigate(Screen.Search.route) },
                    onCategory    = { categoryId -> catalogViewModel.applyQuickFilter(categoryId = categoryId); goToCatalog() },
                    onSeeAllPopular    = { catalogViewModel.applyQuickFilter(sortBy = "rating"); goToCatalog() },
                    onSeeAllNew        = { catalogViewModel.applyQuickFilter(newOnly = true); goToCatalog() },
                    onSeeAllBestOffers = { catalogViewModel.applyQuickFilter(); goToCatalog() },
                )
            }
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onProduct = { id -> rootNavController.navigate(Screen.ProductDetail.createRoute(id)) },
                    onCart    = { rootNavController.navigate(Screen.Cart.route) },
                )
            }
            composable(Screen.LoyaltyCard.route) {
                LoyaltyCardScreen()
            }
            composable(Screen.Bonuses.route) {
                BonusesScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onOrders       = { rootNavController.navigate(Screen.OrderHistory.route) },
                    onFavorites    = { rootNavController.navigate(Screen.Favorites.route) },
                    onNotifications = { rootNavController.navigate(Screen.Notifications.route) },
                    onSelectBranch = { rootNavController.navigate(Screen.SelectBranch.createRoute("settings")) },
                    onLoggedOut    = onLoggedOut,
                )
            }
        }
    }
}

@Composable
private fun RelaxBottomNavBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 20.dp, spotColor = Color(0x1A000000))
            .background(RelaxWhite)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                if (item.isCenter) {
                    // Center card button — elevated
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .offset(y = (-10).dp)
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), spotColor = RelaxDark.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(RelaxDark, RelaxDarkSecondary)))
                            .clickable { onNavigate(item.route) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(item.icon, null, tint = RelaxWhite, modifier = Modifier.size(26.dp))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication        = null,
                                onClick           = { onNavigate(item.route) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        val label = stringResource(item.labelRes)
                        AnimatedContent(targetState = isSelected, label = "nav_icon") { selected ->
                            Icon(
                                imageVector = if (selected) item.iconSelected else item.icon,
                                contentDescription = label,
                                tint = if (selected) RelaxDark else RelaxTextHint,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text       = label,
                            fontSize   = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color      = if (isSelected) RelaxDark else RelaxTextHint,
                        )
                    }
                }
            }
        }
    }
}
