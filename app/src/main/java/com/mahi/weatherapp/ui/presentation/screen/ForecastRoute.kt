package com.mahi.weatherapp.ui.presentation.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mahi.weatherapp.ui.presentation.statesAndEvents.event.ForecastEvent
import com.mahi.weatherapp.ui.presentation.viewmodel.ForecastViewModel
import org.koin.androidx.compose.koinViewModel

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun isLocationGranted(context: android.content.Context): Boolean =
    LOCATION_PERMISSIONS.any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

@Composable
fun ForecastRoute(modifier: Modifier = Modifier, viewModel: ForecastViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Always reflects the latest hasRequestedLocation without re-registering the observer.
    val hasRequestedLocation by rememberUpdatedState(state.hasRequestedLocation)

    // Tap on the location badge → open this app's System Settings page.
    val openAppSettings: () -> Unit = {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        viewModel.onEvent(ForecastEvent.LocationPermissionChanged(granted))
        viewModel.onEvent(
            if (granted) ForecastEvent.UseDeviceLocation else ForecastEvent.UseFallbackCity
        )
    }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Always reflect the real permission state (covers toggling from settings).
                val currentlyGranted = isLocationGranted(context)
                viewModel.onEvent(ForecastEvent.LocationPermissionChanged(currentlyGranted))

                // Only show the system prompt once.
                if (!hasRequestedLocation) {
                    permissionLauncher.launch(LOCATION_PERMISSIONS)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ForecastScreen(
        modifier = modifier,
        state = state,
        onCityChange = { viewModel.onEvent(ForecastEvent.CityChanged(it)) },
        onSearch = { viewModel.onEvent(ForecastEvent.Search) },
        onRetry = { viewModel.onEvent(ForecastEvent.Retry) },
        onRefresh = { viewModel.onEvent(ForecastEvent.Refresh) },
        onForecastDaysChange = { viewModel.onEvent(ForecastEvent.ForecastDaysChanged(it)) },
        onLocationBadgeClick = openAppSettings
    )
}