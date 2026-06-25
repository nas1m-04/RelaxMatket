package tj.dastras.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import tj.dastras.R
import tj.dastras.core.navigation.Route
import tj.dastras.ui.components.activityViewModel
import tj.dastras.ui.screens.bonuses.BonusesScreen
import tj.dastras.ui.screens.catalog.presentation.CatalogScreen
import tj.dastras.ui.screens.catalog.ViewModel.CatalogViewModel
import tj.dastras.ui.screens.home.Presentation.HomeScreen
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
    NavItem(Route.Home.route,        Icons.Rounded.Home,           Icons.Rounded.Home,           R.string.nav_home),
    NavItem(Route.Catalog.route,     Icons.Rounded.GridView,       Icons.Rounded.GridView,       R.string.nav_catalog),
    NavItem(Route.LoyaltyCard.route, Icons.Rounded.CreditCard,     Icons.Rounded.CreditCard,     R.string.nav_card, true),
    NavItem(Route.Bonuses.route,     Icons.Rounded.Stars,          Icons.Rounded.Stars,          R.string.nav_bonuses),
    NavItem(Route.Profile.route,     Icons.Rounded.PersonOutline,  Icons.Rounded.Person,         R.string.nav_profile),
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
                        popUpTo(Route.Home.route) { saveState = true }
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
            startDestination = Route.Home.route,
            modifier         = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition  = { fadeIn(tween(250, easing = EaseOut)) },
            exitTransition   = { fadeOut(tween(200)) },
            popEnterTransition  = { fadeIn(tween(250, easing = EaseOut)) },
            popExitTransition   = { fadeOut(tween(200)) },
        ) {
            composable(Route.Home.route) {
                val catalogViewModel: CatalogViewModel = activityViewModel()
                val goToCatalog: () -> Unit = {
                    bottomNavController.navigate(Route.Catalog.route) {
                        popUpTo(Route.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
                HomeScreen(
                    onProduct     = { id -> rootNavController.navigate(Route.ProductDetail.createRoute(id)) },
                    onCart        = { rootNavController.navigate(Route.Cart.route) },
                    onNotifications = { rootNavController.navigate(Route.Notifications.route) },
                    onPromotions  = { rootNavController.navigate(Route.Promotions.route) },
                    onFavorites   = { rootNavController.navigate(Route.Favorites.route) },
                    onSearch      = { rootNavController.navigate(Route.Search.route) },
                    onCategory    = { categoryId -> catalogViewModel.applyQuickFilter(categoryId = categoryId); goToCatalog() },
                    onSeeAllPopular    = { catalogViewModel.applyQuickFilter(sortBy = "rating"); goToCatalog() },
                    onSeeAllNew        = { catalogViewModel.applyQuickFilter(newOnly = true); goToCatalog() },
                    onSeeAllBestOffers = { catalogViewModel.applyQuickFilter(); goToCatalog() },
                )
            }
            composable(Route.Catalog.route) {
                CatalogScreen(
                    onProduct = { id -> rootNavController.navigate(Route.ProductDetail.createRoute(id)) },
                    onCart    = { rootNavController.navigate(Route.Cart.route) },
                )
            }
            composable(Route.LoyaltyCard.route) {
                LoyaltyCardScreen()
            }
            composable(Route.Bonuses.route) {
                BonusesScreen()
            }
            composable(Route.Profile.route) {
                ProfileScreen(
                    onOrders       = { rootNavController.navigate(Route.OrderHistory.route) },
                    onFavorites    = { rootNavController.navigate(Route.Favorites.route) },
                    onNotifications = { rootNavController.navigate(Route.Notifications.route) },
                    onSelectBranch = { rootNavController.navigate(Route.SelectBranch.createRoute("settings")) },
                    onEditProfile = { rootNavController.navigate(Route.EditProfileScreen.route) },
                    onLoggedOut    = onLoggedOut,
                )
            }
        }
    }
}
@Composable
private fun RelaxBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {

        // background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                )
                .background(
                    color = RelaxWhite.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(26.dp)
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            navItems.forEach { item ->
                val isSelected = currentRoute == item.route

                if (item.isCenter) {
                    CenterNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.route) }
                    )
                } else {
                    BottomNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}
@Composable
private fun RowScope.BottomNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) RelaxDark else Color(0xFF9AA4B2),
        animationSpec = tween(250),
        label = ""
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = ""
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)        // ✅ 36 → 40
                .scale(scale)
                .background(
                    color = if (isSelected)
                        RelaxDark.copy(alpha = 0.08f)
                    else
                        Color.Transparent,
                    shape = RoundedCornerShape(13.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)  // ✅ 20 → 22
            )
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text = stringResource(item.labelRes),
            fontSize = 10.sp,
            color = iconColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )
    }
}
@Composable
private fun RowScope.CenterNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = ""
    )

    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(64.dp)
                .scale(scale)
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = RelaxRed.copy(alpha = 0.35f)
                )
                .background(
                    brush = Brush.linearGradient(
                        listOf(RelaxRed, RelaxOrange)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = RelaxWhite,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}