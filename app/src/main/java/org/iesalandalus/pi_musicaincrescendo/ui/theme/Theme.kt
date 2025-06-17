package org.iesalandalus.pi_musicaincrescendo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Esquema de colores para el tema oscuro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Esquema de colores para el tema claro.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Tema principal de la aplicación.
 *
 * Aplica el esquema de colores correspondiente (claro, oscuro o dinámico) y la tipografía.
 * El color dinámico está disponible en Android 12 y superior.
 *
 * @param darkTheme Indica si se debe usar el tema oscuro. Por defecto, se basa en la configuración del sistema.
 * @param dynamicColor Indica si se debe usar el color dinámico del sistema (Material You). Por defecto es true.
 * @param content El contenido de la UI al que se le aplicará el tema.
 */
@Composable
fun PI_MusicaInCrescendoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Usa color dinámico si está soportado y activado.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Usa el esquema oscuro si está activado.
        darkTheme -> DarkColorScheme
        // Por defecto, usa el esquema claro.
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}