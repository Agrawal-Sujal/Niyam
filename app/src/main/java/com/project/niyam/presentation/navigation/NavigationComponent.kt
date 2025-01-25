package com.project.niyam.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface HomePageNavigation {
    @Serializable
    data object Tasks : HomePageNavigation

    @Serializable
    data object Setting : HomePageNavigation
}

@Serializable
object TasksHomeScreen

@Serializable
data class CreateTaskScreen(val date: String)

@Serializable
data class CreateStrictTaskScreen(val date:String)

@Serializable
object CreateSubStrictTaskScreen

@Serializable
object CreateSubTaskScreen
