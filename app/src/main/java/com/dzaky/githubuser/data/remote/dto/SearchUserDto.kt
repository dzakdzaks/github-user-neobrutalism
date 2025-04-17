package com.dzaky.githubuser.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchUserDto(
    val items: List<UserDto>,

    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("incomplete_results")
    val incompleteResults: Boolean
)