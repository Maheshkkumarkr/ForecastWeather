package com.mahi.weatherapp.data.remote

import com.mahi.weatherapp.domain.error.AppError
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import com.mahi.weatherapp.domain.error.Result


/**
 * Backward-compatible wrapper for existing call sites using HttpResponse.
 *
 * Prefer [safeNetworkCall] for new code.
 */
@Deprecated(message = "Use safeNetworkCall instead")
suspend inline fun <reified T> onSafeCall(
    crossinline execute: suspend () -> HttpResponse
): Result<T, AppError> = safeNetworkCall { execute().body<T>() }
