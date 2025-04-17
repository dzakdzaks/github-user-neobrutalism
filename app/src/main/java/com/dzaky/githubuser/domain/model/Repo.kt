package com.dzaky.githubuser.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val stargazersCount: Int,
    val htmlUrl: String
)