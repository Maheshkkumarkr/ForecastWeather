package com.mahi.weatherapp.data.local

import com.mahi.weatherapp.data.common.safeCall
import com.mahi.weatherapp.domain.error.LocalError
import com.mahi.weatherapp.domain.error.Result

/**
 * Specialized safe wrapper for Room/local datasource calls.
 */
suspend inline fun <T> safeLocalCall(
    crossinline execute: suspend () -> T
): Result<T, LocalError> {
    return safeCall(
        execute = execute,
        mapError = { throwable -> throwable.toLocalError() }
    )
}

