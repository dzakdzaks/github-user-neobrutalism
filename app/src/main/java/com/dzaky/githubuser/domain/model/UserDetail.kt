package com.dzaky.githubuser.domain.model

data class UserDetail(
    val id: Long,
    val profileUrl: String,
    val username: String,
    val avatarUrl: String,
    val fullName: String?,
    val followers: Int,
    val following: Int
)
