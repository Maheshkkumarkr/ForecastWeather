package com.mahi.weatherapp.common

/**
 * Represents the state of a resource that is being loaded, has succeeded, or has failed.
 * Used to encapsulate data along with its loading status and an optional message.
 * @param T The type of data being held.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val fromCache: Boolean = false,
    val lastUpdatedMillis: Long? = null
) {
    class Success<T>(data: T, fromCache: Boolean = false, lastUpdatedMillis: Long? = null) :
        Resource<T>(data = data, fromCache = fromCache, lastUpdatedMillis = lastUpdatedMillis)

    class Error<T>(message: String?, data: T? = null) :
        Resource<T>(data = data, message = message, fromCache = false, lastUpdatedMillis = null)

    class Loading<T>(data: T? = null) :
        Resource<T>(data = data, message = null, fromCache = false, lastUpdatedMillis = null)
}