package com.dzaky.githubuser.ui.userdetail

import com.dzaky.githubuser.domain.model.Repo
import com.dzaky.githubuser.domain.model.UserDetail

data class UserDetailState(
    val isLoading: Boolean = false,
    val isLoadingMoreRepos: Boolean = false,
    val user: UserDetail? = null,
    val repos: List<Repo> = emptyList(),
    val error: String? = null,
    val currentRepoPage: Int = 1,
    val hasNextRepoPage: Boolean = false
)