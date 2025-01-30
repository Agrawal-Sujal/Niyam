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
                onCreateStrictTask = { date, id ->
                    navController.navigate(CreateStrictTaskScreen(date = date, id = id))
                },
                context = context,
                onCreateTask = { date, id ->
                    navController.navigate(CreateTaskScreen(date = date, id = id))
                }
            )
        }

        composable<CreateStrictTaskScreen> {
            changeBottomNavigation(0)
            val date = it.arguments?.getString("date")
            val id = it.arguments?.getString("id") ?: "-1"
            CreateStrictTask(
                viewModel,
                date = date!!,
                onClick = {
                    navController.navigateUp()
                },
                navigateToCreateSubTaskScreen = { idx, id ->
                    navController.navigate(CreateSubStrictTaskScreen(idx = idx, id = id))
                },
                id = id
            )
        }
        composable<CreateTaskScreen> {
            changeBottomNavigation(0)
            val date = it.arguments?.getString("date")
            val id = it.arguments?.getString("id") ?: "-1"
            CreateTask(
                taskViewModel,
                date = date!!,
                onClick = {
                    navController.navigateUp()
                },
                navigateToCreateSubTaskScreen = { idx ->
                    navController.navigate(CreateSubTaskScreen(idx = idx))
                },
                id=id
            )
        }

        composable<CreateSubStrictTaskScreen> {
            val idx = it.arguments?.getString("idx") ?: "-1"
            val id = it.arguments?.getString("id") ?: "-1"
            changeBottomNavigation(0)
            CreateStrictSubTask(
                viewModel, navigateToCreateTaskScreen = {
                    navController.navigateUp()
                }, idx = idx.toInt(),
                id = id.toInt()
            )
        }

        composable<CreateSubTaskScreen> {
            val idx = it.arguments?.getString("idx")?:"-1"
            changeBottomNavigation(0)
            CreateSubTask(taskViewModel, navigateToCreateTaskScreen = {
                navController.navigateUp()
            },idx.toInt())
        }
    }
}
