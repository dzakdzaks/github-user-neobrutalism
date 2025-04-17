package com.dzaky.githubuser.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * Generic helper to collect a Flow<UiState<T>> and update your MutableStateFlow<S>.
 */
inline fun <T, S> CoroutineScope.collectUiState(
    source: Flow<UiState<T>>,
    targetState: MutableStateFlow<S>,
    crossinline onSuccess: S.(T) -> S,
    crossinline onLoading: (S.() -> S),
    crossinline onError: S.(String) -> S
) {
    launch {
        source.collect { result ->
            targetState.update { current ->
                when (result) {
                    is UiState.Loading -> onLoading.invoke(current)
                    is UiState.Success -> current.onSuccess(result.data)
                    is UiState.Error -> current.onError(result.message)
                }
            }
        }
    }
}