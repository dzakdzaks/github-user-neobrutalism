package com.dzaky.githubuser.ui.navigation

sealed interface AppScreen {
    val route: String

    data object UserList : AppScreen {
        private const val BASE = "userList"
        override val route = BASE
    }

    data object UserDetail : AppScreen {
        private const val BASE = "userDetail"
        const val USERNAME_ARG = "username"

        fun routeTo(username: String): String = "$BASE/$username"

        override val route: String = "$BASE/{$USERNAME_ARG}"
    }
}
