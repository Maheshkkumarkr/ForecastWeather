package com.mahi.weatherapp.data.remote

import com.mahi.weatherapp.domain.error.RemoteError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlinx.serialization.SerializationException

/**
 * Maps low-level throwables from remote calls to domain-level [RemoteError].
 */
fun Throwable.toRemoteError(): RemoteError {
    return when (this) {
        is HttpRequestTimeoutException,
        is SocketTimeoutException -> RemoteError.RequestTimeout

        is RedirectResponseException -> RemoteError.Redirection

        is ClientRequestException -> RemoteError.Custom("HTTP ${this.response.status.value} ${this.response.status.description}")

        is ServerResponseException -> RemoteError.Custom("HTTP ${this.response.status.value} ${this.response.status.description}")

        is ResponseException -> RemoteError.Custom("HTTP ${this.response.status.value} ${this.response.status.description}")

        is UnknownHostException,
        is UnresolvedAddressException,
        is ConnectException,
        is NoRouteToHostException,
        is SocketException -> RemoteError.NoInternet

        is SerializationException -> RemoteError.Serialization

        else -> RemoteError.Unknown
    }
}

private fun Int.toRemoteErrorByStatus(): RemoteError {
    return when (this) {
        401 -> RemoteError.Custom("Unauthorized request. Check your OpenWeather API key.")
        408 -> RemoteError.RequestTimeout
        429 -> RemoteError.TooManyRequests
        in 300..399 -> RemoteError.Redirection
        in 400..499 -> RemoteError.Client
        in 500..599 -> RemoteError.Server
        else -> RemoteError.Unknown
    }
}

