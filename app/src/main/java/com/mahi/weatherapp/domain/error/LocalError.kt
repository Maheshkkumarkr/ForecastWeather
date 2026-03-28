package com.mahi.weatherapp.domain.error

/**
 * Represents errors originating from local data sources (e.g., database, file system).
 */
sealed class LocalError(override val message: String) : AppError {
    data object FileNotFound : LocalError("File not found.")
    data object DiskFull : LocalError("Disk is full.")
    data object ReadFailure : LocalError("Read failure.")
    data object Unknown : LocalError("An unknown local error occurred.")
    data class Custom(override val message: String) : LocalError(message)
}

