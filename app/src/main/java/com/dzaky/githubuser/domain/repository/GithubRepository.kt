package com.dzaky.githubuser.domain.repository

import com.dzaky.githubuser.domain.model.PaginatedResponse
import com.dzaky.githubuser.domain.model.Repo
import com.dzaky.githubuser.domain.model.User
import com.dzaky.githubuser.domain.model.UserDetail

interface GitHubRepository {
    suspend fun searchUsers(query: String, page: Int, perPage: Int): PaginatedResponse<User>
    suspend fun getUserDetail(username: String): UserDetail
    suspend fun getUserRepos(username: String, page: Int, perPage: Int): PaginatedResponse<Repo>
}