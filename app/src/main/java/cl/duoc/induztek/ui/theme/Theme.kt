package cl.duoc.induztek.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9CC2F0),
    onPrimary = AzulOscuro,
    primaryContainer = AzulOscuro,
    onPrimaryContainer = AzulClaro
)

private val LightColorScheme = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondaryContainer = AzulClaro,
    onSecondaryContainer = AzulOscuro,
    background = Fondo,
    surface = Fondo,
    onSurfaceVariant = GrisTexto,
    outline = GrisBorde,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    error = Rojo
)

@Composable
fun InduztekProtocolosTheme(
    // Siempre claro, igual que los mockups
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
