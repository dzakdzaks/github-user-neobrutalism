package com.dzaky.githubuser.ui.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzaky.githubuser.common.collectUiState
import com.dzaky.githubuser.domain.usecase.GetUserDetailUseCase
import com.dzaky.githubuser.domain.usecase.GetUserReposUseCase
import com.dzaky.githubuser.ui.navigation.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getUserReposUseCase: GetUserReposUseCase
) : ViewModel() {

    private val username: String = checkNotNull(
        savedStateHandle[AppScreen.UserDetail.USERNAME_ARG]
    ) { "username is required in UserDetailViewModel" }

    private val _state = MutableStateFlow(UserDetailState())
    val state: StateFlow<UserDetailState> = _state.asStateFlow()

    init {
        onEvent(UserDetailEvent.LoadUserData)
    }

    fun onEvent(event: UserDetailEvent) {
        when (event) {
            UserDetailEvent.LoadUserData -> loadUserData()
            UserDetailEvent.LoadNextReposPage -> loadNextReposPage()
            UserDetailEvent.RefreshData -> refreshData()
        }
    }

    private fun loadUserData() {
        loadUserDetail()
        loadUserRepos(1, true)
    }

    private fun refreshData() {
        _state.update {
            it.copy(
                repos = emptyList(),
                currentRepoPage = 1,
                hasNextRepoPage = false
            )
        }
        loadUserData()
    }

    private fun loadUserDetail() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.collectUiState(
            source = getUserDetailUseCase(username),
            targetState = _state,
            onSuccess = { result -> copy(user = result, isLoading = false, error = null) },
            onLoading = { copy(isLoading = true) },
            onError = { message -> copy(error = message, isLoading = false) }
        )
    }

    private fun loadUserRepos(page: Int, resetList: Boolean = false) {
        if (resetList) {
            _state.update { it.copy(isLoading = true, error = null) }
        } else {
            _state.update { it.copy(isLoadingMoreRepos = true) }
        }

        viewModelScope.collectUiState(
            source = getUserReposUseCase(username, page),
            targetState = _state,
            onSuccess = { paginatedResponse ->
                copy(
                    repos = if (resetList) paginatedResponse.data else repos + paginatedResponse.data,
                    isLoading = false,
                    isLoadingMoreRepos = false,
                    currentRepoPage = page,
                    hasNextRepoPage = paginatedResponse.hasNextPage,
                    error = null
                )
            },
            onLoading = { copy() },
            onError = { message ->
                copy(
                    error = message,
                    isLoading = false,
                    isLoadingMoreRepos = false
                )
            }
        )
    }

    private fun loadNextReposPage() {
        if (state.value.hasNextRepoPage && !state.value.isLoadingMoreRepos && !state.value.isLoading) {
            val nextPage = state.value.currentRepoPage + 1
            loadUserRepos(nextPage, false)
        }
    }
}