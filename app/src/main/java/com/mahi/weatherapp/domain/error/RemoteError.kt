package com.mahi.weatherapp.domain.error

/**
 * Represents errors originating from remote sources (e.g., network API calls).
 */
sealed class RemoteError(override val message: String) : AppError {
    data object RequestTimeout : RemoteError("Request timed out. Please try again later.")
    data object TooManyRequests : RemoteError("Too many requests. Please try again later.")
    data object NoInternet : RemoteError("No internet connection. Please check your network.")
    data object Server : RemoteError("Server error. Please try again later.")
    data object Client : RemoteError("Client error. Network is unreachable.")
    data object Redirection : RemoteError("Redirection error.")
    data object Serialization : RemoteError("Serialization error.")
    data object MayBeServerOrNetworkDown : RemoteError("Might be No internet or Server is down")
    data object Unknown : RemoteError("An unknown remote error occurred.")
    data class Custom(override val message: String) : RemoteError(message)
}

