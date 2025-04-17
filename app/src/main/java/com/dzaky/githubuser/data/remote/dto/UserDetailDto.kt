package com.dzaky.githubuser.data.remote.dto

import com.dzaky.githubuser.domain.model.UserDetail
import com.google.gson.annotations.SerializedName

data class UserDetailDto(
    val id: Long,
    @SerializedName("html_url") val profileUrl: String,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("name") val fullName: String?,
    val followers: Int,
    val following: Int
) {
    fun toDomain() = UserDetail(id, profileUrl, username, avatarUrl, fullName, followers, following)
}
