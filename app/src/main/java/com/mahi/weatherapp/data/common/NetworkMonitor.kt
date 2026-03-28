package com.mahi.weatherapp.data.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Observes real network connectivity by tracking validated networks in a Set.
 *
 * A network is considered valid only when it has both:
 * - [NetworkCapabilities.NET_CAPABILITY_INTERNET]  — routable internet path exists
 * - [NetworkCapabilities.NET_CAPABILITY_VALIDATED] — Android confirmed traffic actually flows
 *
 * This avoids false-positives from captive portals, WiFi without internet, etc.
 */
class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isOnline: Flow<Boolean> = callbackFlow {

        // Track each individual network that is currently internet-validated.
        val validatedNetworks = mutableSetOf<Network>()

        fun push() {
            val online = validatedNetworks.isNotEmpty()
            Log.d(TAG, "connectivity update → online=$online (validated=${validatedNetworks.size})")
            trySend(online)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {

            // onAvailable fires when Android "connects" to a network, but it may not
            // have internet yet. Do NOT emit here — wait for onCapabilitiesChanged.
            override fun onAvailable(network: Network) {
                Log.d(TAG, "onAvailable network=$network")
            }

            // Fires when capabilities change — including when VALIDATED is gained/lost.
            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities
            ) {
                val isValidated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                if (isValidated) validatedNetworks.add(network) else validatedNetworks.remove(network)
                push()
            }

            // Fires when a network is fully lost. Remove it unconditionally.
            override fun onLost(network: Network) {
                Log.d(TAG, "onLost network=$network")
                validatedNetworks.remove(network)
                push()
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit accurate initial state synchronously before any callbacks arrive.
        val initiallyOnline = hasValidatedNetwork()
        Log.d(TAG, "initial state → online=$initiallyOnline")
        trySend(initiallyOnline)

        awaitClose {
            Log.d(TAG, "unregistering network callback")
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun hasValidatedNetwork(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private companion object {
        const val TAG = "NetworkMonitor"
    }
}

