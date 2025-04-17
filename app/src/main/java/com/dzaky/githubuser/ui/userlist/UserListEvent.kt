package com.dzaky.githubuser.ui.userlist

sealed class UserListEvent {
    data class Search(val query: String) : UserListEvent()
    data object ForceSearch : UserListEvent()
    data object FirstLoaded: UserListEvent()
    data object LoadNextPage: UserListEvent()
    data object RefreshData: UserListEvent()
}