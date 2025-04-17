package com.dzaky.githubuser.common

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle

interface NavRoute<T : Parcelable> {
    val route: String
    fun createRoute(arg: T): String
    fun parse(savedStateHandle: SavedStateHandle): T
}