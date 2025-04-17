package com.dzaky.githubuser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dzaky.githubuser.ui.userdetail.UserDetailScreen
import com.dzaky.githubuser.ui.userlist.UserListScreen
import com.dzaky.githubuser.ui.userlist.UserListViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "userList",
        modifier = modifier
    ) {
        composable(route = AppScreen.UserList.route) {
            val userListViewModel = hiltViewModel<UserListViewModel>()

            UserListScreen(
                viewModel = userListViewModel,
                onUserClick = { username ->
                    navController.navigate(AppScreen.UserDetail.routeTo(username))
                }
            )
        }
        composable(
            route = AppScreen.UserDetail.route,
            arguments = listOf(navArgument(AppScreen.UserDetail.USERNAME_ARG) {
                type = NavType.StringType
            })
        ) {
            UserDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}