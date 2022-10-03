package com.vk.sarthi.ui.nav

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LeaveBagsAtHome
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vk.sarthi.R
import com.vk.sarthi.ui.screen.*
import com.vk.sarthi.ui.theme.SarthiTheme
import com.vk.sarthi.ui.theme.Teal200

sealed class Screens(val route: String, val icon: ImageVector?) {
    object Login : Screens("login", null)
    object Dashboard : Screens("dashboard", null)
    object Profile : Screens("profile/{id}?isEditable={isEditable}", null)
    object Search : Screens("search", null)
    object ForgetPassword : Screens("forgetPassword", null)
    object ComplaintList : Screens("ComplaintList", Icons.Outlined.ListAlt)
    object ComplaintDetails : Screens("ComplaintDetails/", null)
    object AddComment : Screens("AddComment/", null)
    object DailyVisit : Screens("DailyVisit", Icons.Outlined.CalendarToday)
    object AddDailyVisit : Screens("AddDailyVisit", null)
    object OfficeWork : Screens("OfficeWork", Icons.Outlined.LeaveBagsAtHome)
    object AddDailyWork : Screens("AddDailyWork", null)
    object DailyVisitDetails : Screens("DailyVisitDetails", null)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    BottomNavigation(

        // set background color
        backgroundColor = Teal200
    ) {

        // observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // observe current route to change the icon
        // color,label color when navigated
        val currentRoute = navBackStackEntry?.destination?.route

        // Bottom nav items we declared
        // Place the bottom nav items
        BottomNavigationItem(

            // it currentRoute is equal then its selected route
            selected = currentRoute == Screens.DailyVisit.route,

            // navigate on click
            onClick = {
                navController.navigate(Screens.DailyVisit.route) {
                    popUpTo(0)
                }
            },

            // Icon of navItem
            icon = {

                Icon(
                    imageVector = Screens.DailyVisit.icon!!,
                    contentDescription = stringResource(id = R.string.daily_visit)
                )
            },

            // label
            label = {
                Text(text = stringResource(id = R.string.daily_visit).split("/")[1],
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            },
            alwaysShowLabel = false
        )

        BottomNavigationItem(

            // it currentRoute is equal then its selected route
            selected = currentRoute == Screens.OfficeWork.route,

            // navigate on click
            onClick = {
                navController.navigate(Screens.OfficeWork.route) {
                    popUpTo(0)
                }
            },

            // Icon of navItem
            icon = {
                Icon(
                    imageVector = Screens.OfficeWork.icon!!,
                    contentDescription = stringResource(id = R.string.office_work)
                )
            },

            // label
            label = {
                Text(text = stringResource(id = R.string.office_work).split("/")[1],
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            },
            alwaysShowLabel = false
        )


    }


}


@Composable
fun ShowNavGraph(name: String) {
    val navigator  = rememberNavController()

    NavHost(
        navController = navigator,
        startDestination = name
    ) {
        composable(Screens.Login.route) { LoginScreen(navigator) }
        composable(Screens.Dashboard.route) { Dashboard(navigator) }

        composable(Screens.ForgetPassword.route) { ForgetPassword(navigator) }
        composable(Screens.ComplaintList.route) { ComplaintListUI(navigator) }
        composable(Screens.ComplaintDetails.route+"/{id}") {
            ComplaintDetails(id = it.arguments?.getString("id")!!,navigator) }
        composable(Screens.AddComment.route+"/{id}/{commentId}") {
            AddComment(id = it.arguments?.getString("id")!!,commentid = it.arguments?.getString("commentId")!!,navigator) }
        composable(Screens.DailyVisit.route) { DailyVisit(navigator) }
        composable(Screens.AddDailyVisit.route+"/{id}") { AddDailyVisit(workID = it.arguments?.getString("id")!!,navigator) }
        composable(Screens.OfficeWork.route) { OfficeWork(navigator) }

        composable(Screens.AddDailyWork.route+"/{id}") { AddDailyWork(workID = it.arguments?.getString("id")!!,navigator) }
        composable(Screens.DailyVisitDetails.route+"/{id}") { DailyVisitDetailsUI(workID = it.arguments?.getString("id")!!,navigator) }

        composable(
            route = Screens.Profile.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                },
                navArgument("isEditable") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            ProfileScreen(
                profileId = it.arguments?.getString("id")!!,
                isEditable = it.arguments?.getBoolean("isEditable")!!
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SarthiTheme {
        ShowNavGraph(Screens.Dashboard.route)
    }
}