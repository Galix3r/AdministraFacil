package com.example.proyectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.data.AppScreens
import com.example.proyectofinal.data.UsuarioPrefs
import com.example.proyectofinal.data.database.AppDatabase
import com.example.proyectofinal.data.repository.CategoriaRepository
import com.example.proyectofinal.data.repository.MovimientoRepository
import com.example.proyectofinal.data.repository.UsuarioRepository
import com.example.proyectofinal.ui.DashboardScreen
import com.example.proyectofinal.ui.LoginScreen
import com.example.proyectofinal.ui.NuevoMovimientoScreen
import com.example.proyectofinal.ui.RegistroScreen
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalTheme {
                AppNavegacionAdministraFacil()
            }
        }
    }
}

@Composable
fun AppNavegacionAdministraFacil() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    val usuarioRepository = remember { UsuarioRepository(database.usuarioDao()) }
    val categoriaRepository = remember { CategoriaRepository(database.categoriaDao()) }
    val movimientoRepository = remember { MovimientoRepository(database.movimientoDao()) }
    val usuarioPrefs = remember { UsuarioPrefs(context) }

    var idUsuarioActual by remember { mutableIntStateOf(usuarioPrefs.obtenerUsuarioId()) }

    val navController = rememberNavController()

    val startDestination = if (idUsuarioActual > 0) AppScreens.Dashboard.route else AppScreens.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppScreens.Login.route) {
            LoginScreen(
                usuarioRepository = usuarioRepository,
                onLoginExitoso = { id ->
                    usuarioPrefs.guardarUsuarioId(id)
                    idUsuarioActual = id
                    navController.navigate(AppScreens.Dashboard.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(AppScreens.Registro.route)
                }
            )
        }

        composable(AppScreens.Registro.route) {
            RegistroScreen(
                usuarioRepository = usuarioRepository,
                onRegistroExitoso = { id ->
                    usuarioPrefs.guardarUsuarioId(id)
                    idUsuarioActual = id
                    navController.navigate(AppScreens.Dashboard.route) {
                        popUpTo(AppScreens.Registro.route) { inclusive = true }
                    }
                },
                onVolverALogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppScreens.Dashboard.route) {
            if (idUsuarioActual > 0) {
                DashboardScreen(
                    idUsuario = idUsuarioActual,
                    usuarioRepository = usuarioRepository,
                    movimientoRepository = movimientoRepository,
                    categoriaRepository = categoriaRepository,
                    onNuevoMovimiento = {
                        navController.navigate(AppScreens.NuevoMovimiento.route)
                    },
                    onCerrarSesion = {
                        usuarioPrefs.cerrarSesion()
                        idUsuarioActual = -1
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(AppScreens.Dashboard.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(AppScreens.NuevoMovimiento.route) {
            if (idUsuarioActual > 0) {
                NuevoMovimientoScreen(
                    idUsuario = idUsuarioActual,
                    movimientoRepository = movimientoRepository,
                    categoriaRepository = categoriaRepository,
                    onMovimientoRegistrado = {
                        navController.popBackStack()
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
