package com.example.proyectofinal.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.proyectofinal.data.UsuarioPrefs
import com.example.proyectofinal.data.database.AppDatabase
import com.example.proyectofinal.data.repository.CategoriaRepository
import com.example.proyectofinal.data.repository.MovimientoRepository

@Composable
fun SegundaPantalla(navController: NavController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val categoriaRepository = remember { CategoriaRepository(database.categoriaDao()) }
    val movimientoRepository = remember { MovimientoRepository(database.movimientoDao()) }
    val usuarioPrefs = remember { UsuarioPrefs(context) }
    val idUsuario = usuarioPrefs.obtenerUsuarioId()

    NuevoMovimientoScreen(
        idUsuario = if (idUsuario > 0) idUsuario else 1,
        movimientoRepository = movimientoRepository,
        categoriaRepository = categoriaRepository,
        onMovimientoRegistrado = { navController.popBackStack() },
        onVolver = { navController.popBackStack() }
    )
}
