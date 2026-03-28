package com.mahi.weatherapp.data.remote

import android.util.Log
import com.mahi.weatherapp.data.common.safeCall
import com.mahi.weatherapp.domain.error.RemoteError
import com.mahi.weatherapp.domain.error.Result

/**
 * Specialized safe wrapper for remote/Ktor calls.
 */
suspend inline fun <T> safeNetworkCall(
    crossinline execute: suspend () -> T
): Result<T, RemoteError> {
    return safeCall(
        execute = execute,
        mapError = { throwable ->
            Log.d("safeNetworkCall", "safeNetworkCall error: ${throwable.message}", throwable)
            throwable.toRemoteError()
        }
    )
}


