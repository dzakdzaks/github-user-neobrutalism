package com.dzaky.githubuser.domain.usecase

import com.dzaky.githubuser.common.IoDispatcher
import com.dzaky.githubuser.common.UiState
import com.dzaky.githubuser.domain.model.PaginatedResponse
import com.dzaky.githubuser.domain.model.User
import com.dzaky.githubuser.domain.repository.GitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: GitHubRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        query: String,
        page: Int = 1,
        perPage: Int = 30
    ): Flow<UiState<PaginatedResponse<User>>> = flow {
        emit(UiState.Loading)
        try {
            val paginatedUsers = repository.searchUsers(query, page, perPage)
            emit(UiState.Success(paginatedUsers))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(ioDispatcher)
}