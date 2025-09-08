package com.project.niyam.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.niyam.ui.screens.addTimeBoundTask.AddTimeBoundTaskScreen
import com.project.niyam.ui.screens.auth.LoginScreen
import com.project.niyam.ui.screens.auth.RegisterScreen
import com.project.niyam.ui.screens.flexibleTask.AddFlexibleTaskScreen
import com.project.niyam.ui.screens.friends.FriendsScreen
import com.project.niyam.ui.screens.home.HomeScreen
import com.project.niyam.ui.screens.profile.ProfileScreen
import com.project.niyam.ui.screens.runningTask.TaskScreen
import com.project.niyam.ui.theme.NiyamColors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val startDestination =
        if (isLoggedIn) NavRoutes.HomeScreen.route else NavRoutes.LoginScreen.route
    val bottomBarRoutes = listOf(
        NavRoutes.HomeScreen.route,
        NavRoutes.ProfileScreen.route,
    )

    val showBottomBar = currentRoute in bottomBarRoutes
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute,
                )
            }
        },
        floatingActionButton = {
            if (currentRoute == NavRoutes.HomeScreen.route) {
                ExpandableFab(
                    onAddStrict = {
                        navController.navigate(NavRoutes.AddTimeBoundedTask.route)
                    },
                    onAddFlexible = {
                        navController.navigate(NavRoutes.AddFlexibleTask.route)
                    },
                )
            }
        },
        modifier = Modifier.fillMaxSize().background(color = NiyamColors.backgroundColor),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = NavRoutes.HomeScreen.route) {
                HomeScreen(
                    onUpdateTask = { t1, t2 ->
                    },
                    onTimeBoundClicked = { taskId ->
                        navController.navigate(
                            NavRoutes.TaskScreen.createRoute(taskId = taskId, isFlexible = false),
                        )
                    },
                    onFlexibleClicked = { taskId ->
                        navController.navigate(
                            NavRoutes.TaskScreen.createRoute(taskId = taskId, isFlexible = true),
                        )
                    },
                )
            }
            composable(route = NavRoutes.AddTimeBoundedTask.route) {
                AddTimeBoundTaskScreen(onNavigateBack = {
                    navController.popBackStack()
                })
            }
            composable(route = NavRoutes.AddFlexibleTask.route) {
                AddFlexibleTaskScreen(onNavigateBack = {
                    navController.popBackStack()
                })
            }
            composable(
                route = NavRoutes.TaskScreen.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType },
                    navArgument("isFlexible") { type = NavType.BoolType },
                ),
            ) {
                TaskScreen()
            }

            composable(NavRoutes.LoginScreen.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavRoutes.HomeScreen.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(NavRoutes.RegisterScreen.route) },
                )
            }
            composable(NavRoutes.RegisterScreen.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(NavRoutes.HomeScreen.route) {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.navigate(NavRoutes.LoginScreen.route) },
                )
            }
            composable(NavRoutes.ProfileScreen.route) {
                ProfileScreen(
                    onNavigateToFriends = {
                        navController.navigate(NavRoutes.FriendsScreen.route)
                    },
                )
            }

            composable(NavRoutes.FriendsScreen.route) {
                FriendsScreen()
            }
        }
    }
}

@Composable
private fun ExpandableFab(
    onAddStrict: () -> Unit,
    onAddFlexible: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        if (expanded) {
            ExtendedFloatingActionButton(
                onClick = {
                    expanded = false
                    onAddStrict()
                },
                text = { Text("Strict Task") },
                modifier = Modifier.padding(bottom = 8.dp),
                icon = {},

            )
            ExtendedFloatingActionButton(
                onClick = {
                    expanded = false
                    onAddFlexible()
                },
                text = { Text("Regular Task") },
                modifier = Modifier.padding(bottom = 8.dp),
                icon = {
                },
            )
        }
        FloatingActionButton(onClick = { expanded = !expanded }, containerColor = NiyamColors.primaryColor) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = NiyamColors.whiteColor)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    NavigationBar(containerColor = NiyamColors.surfaceBackgroundColor) {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HomeScreen.route,
            onClick = {
                navController.navigate(NavRoutes.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NiyamColors.blueColor, selectedTextColor = NiyamColors.blueColor, unselectedIconColor = NiyamColors.greyColor, unselectedTextColor = NiyamColors.greyColor, indicatorColor = MaterialTheme.colorScheme.primary),
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.ProfileScreen.route,
            onClick = {
                navController.navigate(NavRoutes.ProfileScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
        )
    }
}
