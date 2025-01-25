package com.project.niyam.presentation.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.niyam.presentation.screens.view.tasks.CreateStrictTask
import com.project.niyam.presentation.screens.view.tasks.CreateStrictSubTask
import com.project.niyam.presentation.screens.view.tasks.CreateSubTask
import com.project.niyam.presentation.screens.view.tasks.CreateTask
import com.project.niyam.presentation.screens.view.tasks.TasksHomeScreen
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateStrictTaskViewModel
import com.project.niyam.presentation.screens.viewmodels.tasks.CreateTaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksScreenNavigation(
    changeBottomNavigation: (Int) -> Unit,
    context: Context,
    viewModel: CreateStrictTaskViewModel,
    taskViewModel: CreateTaskViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TasksHomeScreen,
    ) {
        composable<TasksHomeScreen> {
            changeBottomNavigation(1)
            TasksHomeScreen(
                onCreateStrictTask = { navController.navigate(CreateStrictTaskScreen(date = it)) },
                context = context,
                onCreateTask = { navController.navigate(CreateTaskScreen(date = it)) }
            )
        }

        composable<CreateStrictTaskScreen> {
            changeBottomNavigation(0)
            val date = it.arguments?.getString("date")
            CreateStrictTask(
                viewModel,
                date = date!!,
                onClick = {
                    navController.navigate(TasksHomeScreen)
                },
                navigateToCreateSubTaskScreen = { navController.navigate(CreateSubStrictTaskScreen) },
            )
        }
        composable<CreateTaskScreen> {
            changeBottomNavigation(0)
            val date = it.arguments?.getString("date")
            CreateTask(
                taskViewModel,
                date = date!!,
                onClick = {
                    navController.navigate(TasksHomeScreen)
                },
                navigateToCreateSubTaskScreen = { navController.navigate(CreateSubTaskScreen) },
            )
        }

        composable<CreateSubStrictTaskScreen> {
            changeBottomNavigation(0)
            CreateStrictSubTask(viewModel, navigateToCreateTaskScreen = {
                navController.navigateUp()
            })
        }

        composable<CreateSubTaskScreen> {
            changeBottomNavigation(0)
            CreateSubTask(taskViewModel, navigateToCreateTaskScreen = {
                navController.navigateUp()
            })
        }
    }
}
