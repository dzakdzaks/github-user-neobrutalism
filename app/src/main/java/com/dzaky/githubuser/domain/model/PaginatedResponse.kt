package com.dzaky.githubuser.domain.model

data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val perPage: Int,
    val totalCount: Int,
    val hasNextPage: Boolean
)