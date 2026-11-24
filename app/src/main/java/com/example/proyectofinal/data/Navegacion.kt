package com.example.proyectofinal.data

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object Registro : AppScreens("registro")
    object Dashboard : AppScreens("dashboard")
    object NuevoMovimiento : AppScreens("nuevo_movimiento")

    // Rutas legacy de apoyo
    object Primera : AppScreens("pantalla_1")
    object Segunda : AppScreens("pantalla_2")
}
