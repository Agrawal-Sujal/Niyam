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
data class CreateTaskScreen(val date: String, val id: String)

@Serializable
data class CreateStrictTaskScreen(val date: String, val id: String)

@Serializable
data class CreateSubStrictTaskScreen(val idx: String, val id: String)

@Serializable
data class CreateSubTaskScreen(val idx: String)
