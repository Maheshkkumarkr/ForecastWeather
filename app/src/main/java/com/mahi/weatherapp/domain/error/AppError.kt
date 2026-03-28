package com.mahi.weatherapp.domain.error

/**
 * A sealed interface representing all possible domain-specific errors in the application.
 */
sealed interface AppError {
    val message: String
}