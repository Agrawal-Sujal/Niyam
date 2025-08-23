package com.project.niyam.ui.navigation

sealed class NavRoutes(val route: String) {
    object HomeScreen : NavRoutes("home")

    object AddTimeBoundedTask : NavRoutes("addTimeBoundedTask")

    object AddFlexibleTask : NavRoutes("addFlexibleTask")

    object TaskScreen : NavRoutes("task/{taskId}/{isFlexible}") {
        fun createRoute(taskId: Int, isFlexible: Boolean): String {
            return "task/$taskId/$isFlexible"
        }
    }

    object LoginScreen : NavRoutes("login")

    object RegisterScreen : NavRoutes("register")
}
