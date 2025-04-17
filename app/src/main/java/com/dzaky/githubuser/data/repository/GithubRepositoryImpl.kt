package com.dzaky.githubuser.data.repository

import com.dzaky.githubuser.data.remote.GitHubApi
import com.dzaky.githubuser.domain.model.PaginatedResponse
import com.dzaky.githubuser.domain.model.Repo
import com.dzaky.githubuser.domain.model.User
import com.dzaky.githubuser.domain.model.UserDetail
import com.dzaky.githubuser.domain.repository.GitHubRepository
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val api: GitHubApi
) : GitHubRepository {

    override suspend fun searchUsers(query: String, page: Int, perPage: Int): PaginatedResponse<User> {
        val response = api.searchUsers(query, page, perPage)
        val users = response.items.map { it.toDomain() }

        return PaginatedResponse(
            data = users,
            page = page,
            perPage = perPage,
            totalCount = response.totalCount,
            hasNextPage = users.size == perPage && (page * perPage) < response.totalCount
        )
    }

    override suspend fun getUserDetail(username: String): UserDetail {
        return api.getUserDetail(username).toDomain()
    }

    override suspend fun getUserRepos(username: String, page: Int, perPage: Int): PaginatedResponse<Repo> {
        val repos = api.getUserRepos(username, page, perPage)

        // Since GitHub API doesn't return total count in the repository endpoint,
        // determine if there's a next page based on the number of items received
        val hasNextPage = repos.size == perPage

        return PaginatedResponse(
            data = repos.filterNot { it.fork }
                .map { it.toDomain() },
            page = page,
            perPage = perPage,
            totalCount = -1, // GitHub API doesn't provide this information directly
            hasNextPage = hasNextPage
        )
    }
}