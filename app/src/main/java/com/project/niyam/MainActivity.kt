package com.project.niyam

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.niyam.presentation.navigation.HomePageNavigation
import com.project.niyam.presentation.navigation.TasksScreenNavigation
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateStrictTaskViewModel
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel
import com.project.niyam.ui.theme.NiyamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navigationList: List<BottomNavigationItem> = listOf(
            BottomNavigationItem(
                title = "Task",
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications,
                destination = HomePageNavigation.Tasks,
            ),
            BottomNavigationItem(
                title = "Settings",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                destination = HomePageNavigation.Setting,
            ),
        )
        setContent {
            val navController = rememberNavController()
            var selectedItemIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
            var showBottomNavigation by rememberSaveable {
                mutableIntStateOf(1)
            }
            val viewModel: CreateStrictTaskViewModel = hiltViewModel()
            val taskViewModel: CreateTaskViewModel = hiltViewModel()
            NiyamTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNavigation == 1) {
                            NavigationBar {
                                navigationList.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        label = {
                                            Text(item.title)
                                        },
                                        onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.destination)
                                        },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (item.hasBadge) Badge()
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = null,
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    },
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = HomePageNavigation.Tasks,
                            enterTransition = { fadeIn(animationSpec = tween(0)) },
                            exitTransition = { fadeOut(animationSpec = tween(0)) },
                        ) {
                            composable<HomePageNavigation.Tasks> {
                                TasksScreenNavigation(
                                    { showBottomNavigation = it },
                                    context = this@MainActivity,
                                    viewModel,
                                    taskViewModel
                                )
                            }
                            composable<HomePageNavigation.Setting> {
                            }
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasBadge: Boolean = false,
    val destination: HomePageNavigation,
)
