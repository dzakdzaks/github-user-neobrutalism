package com.dzaky.githubuser.ui.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzaky.githubuser.common.UiState
import com.dzaky.githubuser.domain.usecase.GetUserDetailUseCase
import com.dzaky.githubuser.domain.usecase.GetUserReposUseCase
import com.dzaky.githubuser.ui.navigation.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

        viewModelScope.launch {
            getUserDetailUseCase(username).collect { result ->
                when (result) {
                    is UiState.Success -> {
                        _state.update { it.copy(user = result.data, isLoading = false, error = null) }
                    }
                    is UiState.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is UiState.Error -> {
                        _state.update { it.copy(error = result.message, isLoading = false) }
                    }
                }
            }
        }
    }

    private fun loadUserRepos(page: Int, resetList: Boolean = false) {
        if (resetList) {
            _state.update { it.copy(isLoading = true, error = null) }
        } else {
            _state.update { it.copy(isLoadingMoreRepos = true) }
        }

        viewModelScope.launch {
            getUserReposUseCase(username, page, 20).collect { result ->
                when (result) {
                    is UiState.Success -> {
                        val paginatedResponse = result.data
                        _state.update {
                            it.copy(
                                repos = if (resetList) paginatedResponse.data else it.repos + paginatedResponse.data,
                                isLoading = false,
                                isLoadingMoreRepos = false,
                                currentRepoPage = page,
                                hasNextRepoPage = paginatedResponse.hasNextPage,
                                error = null
                            )
                        }
                    }
                    is UiState.Loading -> {
                        // Loading state is already set before collecting
                    }
                    is UiState.Error -> {
                        _state.update {
                            it.copy(
                                error = result.message,
                                isLoading = false,
                                isLoadingMoreRepos = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadNextReposPage() {
        if (state.value.hasNextRepoPage && !state.value.isLoadingMoreRepos && !state.value.isLoading) {
            val nextPage = state.value.currentRepoPage + 1
            loadUserRepos(nextPage, false)
        }
    }
}