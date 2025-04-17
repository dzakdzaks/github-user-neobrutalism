package com.dzaky.githubuser.ui.userlist

import com.dzaky.githubuser.domain.model.User

data class UserListState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val isFirstLoad: Boolean = true,
    val currentPage: Int = 1,
    val totalCount: Int = 0,
    val hasNextPage: Boolean = false
)