package com.dzaky.githubuser.data.remote.dto

import com.dzaky.githubuser.domain.model.Repo
import com.google.gson.annotations.SerializedName

data class RepoDto(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("html_url") val htmlUrl: String,
    val fork: Boolean
) {
    fun toDomain(): Repo = Repo(id, name, description, language, stargazersCount, htmlUrl)
}