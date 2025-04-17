package com.dzaky.githubuser.data.remote

import com.dzaky.githubuser.data.remote.dto.RepoDto
import com.dzaky.githubuser.data.remote.dto.SearchUserDto
import com.dzaky.githubuser.data.remote.dto.UserDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchUserDto

    @GET("users/{username}")
    suspend fun getUserDetail(@Path("username") username: String): UserDetailDto

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<RepoDto>
}