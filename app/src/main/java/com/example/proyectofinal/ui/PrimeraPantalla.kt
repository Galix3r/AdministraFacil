package com.example.proyectofinal.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.proyectofinal.data.AppScreens
import com.example.proyectofinal.data.UsuarioPrefs
import com.example.proyectofinal.data.database.AppDatabase
import com.example.proyectofinal.data.repository.CategoriaRepository
import com.example.proyectofinal.data.repository.MovimientoRepository
import com.example.proyectofinal.data.repository.UsuarioRepository

@Composable
fun PrimeraPantalla(navController: NavController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val usuarioRepository = remember { UsuarioRepository(database.usuarioDao()) }
    val categoriaRepository = remember { CategoriaRepository(database.categoriaDao()) }
    val movimientoRepository = remember { MovimientoRepository(database.movimientoDao()) }
    val usuarioPrefs = remember { UsuarioPrefs(context) }
    val idUsuario = usuarioPrefs.obtenerUsuarioId()

    if (idUsuario > 0) {
        DashboardScreen(
            idUsuario = idUsuario,
            usuarioRepository = usuarioRepository,
            movimientoRepository = movimientoRepository,
            categoriaRepository = categoriaRepository,
            onNuevoMovimiento = { navController.navigate(AppScreens.NuevoMovimiento.route) },
            onCerrarSesion = {
                usuarioPrefs.cerrarSesion()
                navController.navigate(AppScreens.Login.route) {
                    popUpTo(AppScreens.Primera.route) { inclusive = true }
                }
            }
        )
    } else {
        LoginScreen(
            usuarioRepository = usuarioRepository,
            onLoginExitoso = { id ->
                usuarioPrefs.guardarUsuarioId(id)
                navController.navigate(AppScreens.Dashboard.route)
            },
            onIrARegistro = {
                navController.navigate(AppScreens.Registro.route)
            }
        )
    }
}
