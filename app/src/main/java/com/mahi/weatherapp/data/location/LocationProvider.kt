package com.mahi.weatherapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationProvider(
    private val context: Context,
    private val client: FusedLocationProviderClient
) {

    suspend fun currentCity(): LocationResult = withContext(Dispatchers.IO) {
        val location = fetchLocation() ?: return@withContext LocationResult.Failure("Unable to detect current location")
        val city = resolveCityName(location)
        if (city.isNullOrBlank()) {
            LocationResult.Failure("Unable to resolve city from location")
        } else {
            LocationResult.Success(city, location.latitude, location.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchLocation(): Location? {
        val current = runCatching { getCurrentLocation() }.getOrNull()
        if (current != null) return current
        return runCatching { getLastKnownLocation() }.getOrNull()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val tokenSource = CancellationTokenSource()
        val task = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, tokenSource.token)
        task.addOnSuccessListener { location -> if (cont.isActive) cont.resume(location) }
        task.addOnFailureListener { if (cont.isActive) cont.resume(null) }
        task.addOnCanceledListener { if (cont.isActive) cont.cancel() }
        cont.invokeOnCancellation { tokenSource.cancel() }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        val task = client.lastLocation
        task.addOnSuccessListener { location -> if (cont.isActive) cont.resume(location) }
        task.addOnFailureListener { if (cont.isActive) cont.resume(null) }
        task.addOnCanceledListener { if (cont.isActive) cont.cancel() }
    }

    @Suppress("DEPRECATION")
    private fun resolveCityName(location: Location): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = runCatching {
            geocoder.getFromLocation(location.latitude, location.longitude, 1)
        }.getOrNull()
        val address = addresses?.firstOrNull()
        return address?.locality ?: address?.subAdminArea ?: address?.adminArea
    }
}

sealed class LocationResult {
    data class Success(val city: String, val latitude: Double, val longitude: Double) : LocationResult()
    data class Failure(val reason: String) : LocationResult()
}
