package com.mahi.weatherapp.data.local

import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.mahi.weatherapp.domain.error.LocalError
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Maps local/Room related throwables to domain-level [LocalError].
 */
fun Throwable.toLocalError(): LocalError {
    return when (this) {
        is FileNotFoundException -> LocalError.FileNotFound
        is SQLiteFullException -> LocalError.DiskFull
        is SQLiteException,
        is IOException -> LocalError.ReadFailure
        else -> LocalError.Unknown
    }
}

