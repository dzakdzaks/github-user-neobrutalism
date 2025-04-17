package com.dzaky.githubuser.domain.usecase

import com.dzaky.githubuser.common.IoDispatcher
import com.dzaky.githubuser.common.UiState
import com.dzaky.githubuser.domain.model.PaginatedResponse
import com.dzaky.githubuser.domain.model.Repo
import com.dzaky.githubuser.domain.repository.GitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserReposUseCase @Inject constructor(
    private val repository: GitHubRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        username: String,
        page: Int = 1,
        perPage: Int = 30
    ): Flow<UiState<PaginatedResponse<Repo>>> = flow {
        emit(UiState.Loading)
        try {
            val paginatedRepos = repository.getUserRepos(username, page, perPage)
            emit(UiState.Success(paginatedRepos))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(ioDispatcher)
}