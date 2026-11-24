package com.example.proyectofinal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaFestival = darkColorScheme(
    primary = NeonVioleta,
    onPrimary = Color.White,
    secondary = NeonCian,
    onSecondary = FondoOscuro,
    tertiary = NeonRosa,
    onTertiary = Color.White,
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = SuperficieOscura,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = TextoClaro
)

@Composable
fun ProyectoFinalTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaFestival,
        typography = Typography,
        content = content
    )
}