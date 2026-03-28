package com.mahi.weatherapp.domain.error

/**
 * A generic wrapper for operations that can either succeed with data [T] or fail with an error [E].
 */
sealed class Result<out T, out E> {
    data class Success<out T>(val data: T) : Result<T, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()
}

inline fun <T, E : AppError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

inline fun <T, E : AppError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <T, E : AppError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) {
        action(error)
    }
    return this
}