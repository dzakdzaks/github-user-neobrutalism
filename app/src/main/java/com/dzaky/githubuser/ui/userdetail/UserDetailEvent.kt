package com.dzaky.githubuser.ui.userdetail

sealed class UserDetailEvent {
    data object LoadUserData : UserDetailEvent()
    data object LoadNextReposPage : UserDetailEvent()
    data object RefreshData : UserDetailEvent()
}