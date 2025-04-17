@file:OptIn(FlowPreview::class)

package com.dzaky.githubuser.ui.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzaky.githubuser.common.collectUiState
import com.dzaky.githubuser.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    init {
        observeDebouncedSearch()
    }

    fun onEvent(event: UserListEvent) {
        when (event) {
            is UserListEvent.Search -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        currentPage = 1,  // Reset page when search query changes
                        users = emptyList() // Clear previous results
                    )
                }
                queryFlow.value = event.query
            }

            is UserListEvent.ForceSearch -> {
                if (state.value.searchQuery.isNotBlank()) {
                    _state.update { it.copy(currentPage = 1, users = emptyList()) }
                    searchUsers(state.value.searchQuery, 1)
                }
            }

            is UserListEvent.FirstLoaded -> {
                _state.update { it.copy(isFirstLoad = false) }
            }

            is UserListEvent.LoadNextPage -> {
                if (state.value.hasNextPage && !state.value.isLoadingMore && !state.value.isLoading) {
                    val nextPage = state.value.currentPage + 1
                    loadNextPage(state.value.searchQuery, nextPage)
                }
            }

            is UserListEvent.RefreshData -> {
                if (state.value.searchQuery.isNotBlank()) {
                    _state.update { it.copy(currentPage = 1, users = emptyList()) }
                    searchUsers(state.value.searchQuery, 1)
                }
            }
        }
    }

    private fun observeDebouncedSearch() {
        queryFlow
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    _state.update { it.copy(currentPage = 1, users = emptyList()) }
                    searchUsers(query, 1)
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            users = emptyList(),
                            totalCount = 0,
                            hasNextPage = false
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchUsers(query: String, page: Int) {
        if (query.isBlank()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = null,
                    users = emptyList(),
                    totalCount = 0,
                    hasNextPage = false
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.collectUiState(
            source = searchUsersUseCase(query, page),
            targetState = _state,
            onSuccess = { paginatedResponse ->
                copy(
                    users = users + paginatedResponse.data,
                    isLoading = false,
                    isLoadingMore = false,
                    currentPage = paginatedResponse.page,
                    totalCount = paginatedResponse.totalCount,
                    hasNextPage = paginatedResponse.hasNextPage,
                    error = null
                )
            },
            onLoading = {
                copy(isLoading = page == 1, isLoadingMore = page > 1)
            },
            onError = { message ->
                copy(error = message, isLoading = false, isLoadingMore = false)
            }
        )
    }

    private fun loadNextPage(query: String, page: Int) {
        _state.update { it.copy(isLoadingMore = true) }
        searchUsers(query, page)
    }
}