package com.dzaky.githubuser.data.remote.dto

import com.dzaky.githubuser.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Long,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String
) {
    fun toDomain(): User = User(id, username, avatarUrl)
}