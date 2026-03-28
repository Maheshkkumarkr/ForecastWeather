package com.mahi.weatherapp.data.common

import com.mahi.weatherapp.domain.error.AppError
import com.mahi.weatherapp.domain.error.Result
import kotlinx.coroutines.CancellationException

/**
 * Generic safe executor that wraps a suspend block and maps failures to a domain error.
 */
suspend inline fun <T, E : AppError> safeCall(
    crossinline execute: suspend () -> T,
    crossinline mapError: (Throwable) -> E
): Result<T, E> {
    return try {
        Result.Success(execute())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.Error(mapError(e))
    }
}

