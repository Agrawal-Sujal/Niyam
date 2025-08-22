package com.project.niyam.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.niyam.ui.screens.addTimeBoundTask.AddTimeBoundTaskScreen
import com.project.niyam.ui.screens.flexibleTask.AddFlexibleTaskScreen
import com.project.niyam.ui.screens.home.HomeScreen
import com.project.niyam.ui.screens.runningTask.TaskScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val showBottomBar =
        (currentRoute == NavRoutes.HomeScreen.route)
    Scaffold(floatingActionButton = {
        if (showBottomBar)
            ExpandableFab(onAddStrict = {
                navController.navigate(NavRoutes.AddTimeBoundedTask.route)
            }, onAddFlexible = {
                navController.navigate(NavRoutes.AddFlexibleTask.route)
            })
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HomeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavRoutes.HomeScreen.route) {
                HomeScreen(
                    onUpdateTask = { t1, t2 ->

                    },
                    onTimeBoundClicked = { taskId ->
                        navController.navigate(
                            NavRoutes.TaskScreen.createRoute(taskId = taskId, isFlexible = false)
                        )
                    },
                    onFlexibleClicked = { taskId ->
                        navController.navigate(
                            NavRoutes.TaskScreen.createRoute(taskId = taskId, isFlexible = true)
                        )
                    }
                )
            }
            composable(route = NavRoutes.AddTimeBoundedTask.route) {
                AddTimeBoundTaskScreen(onBack = {
                    navController.popBackStack()
                }, onTaskAdded = { navController.popBackStack() })
            }
            composable(route = NavRoutes.AddFlexibleTask.route) {
                AddFlexibleTaskScreen(onBack = {
                    navController.popBackStack()
                })
            }
            composable(
                route = NavRoutes.TaskScreen.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType },
                    navArgument("isFlexible") { type = NavType.BoolType }
                )
            ) {
                TaskScreen()
            }
        }

    }
}

@Composable
private fun ExpandableFab(
    onAddStrict: () -> Unit,
    onAddFlexible: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        if (expanded) {
            ExtendedFloatingActionButton(
                onClick = { expanded = false; onAddStrict() },
                text = { Text("Strict Task") },
                modifier = Modifier.padding(bottom = 8.dp),
                icon = {}

            )
            ExtendedFloatingActionButton(
                onClick = { expanded = false; onAddFlexible() },
                text = { Text("Regular Task") },
                modifier = Modifier.padding(bottom = 8.dp),
                icon = {

                }
            )
        }
        FloatingActionButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}