//package com.mahi.weatherapp.ui.theme
//
//import android.app.Activity
//import android.os.Build
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//
//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)
//
//@Composable
//fun WeatherAppTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}

package com.mahi.weatherapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 1. EXTEND MATERIAL THEME: Define our custom color bucket
@Immutable
data class CustomColors(
    val successContainer: Color,
    val onSuccessContainer: Color
)

// 2. CREATE THE BRIDGE: This allows Compose to pass our colors down the tree
val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified
    )
}

// 3. MAP THE DARK PALETTE
private val DarkColorScheme = darkColorScheme(
    primary = WeatherBlueDark,
    onPrimary = WeatherOnBlueDark,
    secondary = WeatherSecondaryDark,
    background = WeatherBackgroundDark,
    surface = WeatherSurfaceDark,
    onBackground = WeatherOnSurfaceDark,
    onSurface = WeatherOnSurfaceDark,
    surfaceVariant = WeatherSurfaceVariantDark,
    errorContainer = Color(0xFF93000A), // Deep Red
    onErrorContainer = Color(0xFFFFDAD6)
)

// 4. MAP THE LIGHT PALETTE
private val LightColorScheme = lightColorScheme(
    primary = WeatherBlueLight,
    onPrimary = WeatherOnBlueLight,
    secondary = WeatherSecondaryLight,
    background = WeatherBackgroundLight,
    surface = WeatherSurfaceLight,
    onBackground = WeatherOnSurfaceLight,
    onSurface = WeatherOnSurfaceLight,
    surfaceVariant = WeatherSurfaceVariantLight,
    errorContainer = Color(0xFFFFDAD6), // Soft Pastel Red
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Android 12+ Monet colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Assign our custom colors based on the theme
    val customColors = if (darkTheme) {
        CustomColors(successContainer = SuccessContainerDark, onSuccessContainer = OnSuccessContainerDark)
    } else {
        CustomColors(successContainer = SuccessContainerLight, onSuccessContainer = OnSuccessContainerLight)
    }

    // 5. THE WRAPPER: Inject our custom colors alongside MaterialTheme
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography, // Assuming you have your Typography file
            content = content
        )
    }
}

// 6. THE SHORTCUT: Allows us to call MaterialTheme.customColors anywhere!
val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current


