package com.dzaky.githubuser.domain.usecase

import com.dzaky.githubuser.common.IoDispatcher
import com.dzaky.githubuser.common.UiState
import com.dzaky.githubuser.domain.model.UserDetail
import com.dzaky.githubuser.domain.repository.GitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor(
    private val repository: GitHubRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(username: String): Flow<UiState<UserDetail>> = flow {
        emit(UiState.Loading)
        try {
            val user = repository.getUserDetail(username)
            emit(UiState.Success(user))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(ioDispatcher)
}
