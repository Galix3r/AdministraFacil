package com.example.proyectofinal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.R
import com.example.proyectofinal.data.entity.Categoria
import com.example.proyectofinal.data.entity.MovimientoConCategoria
import com.example.proyectofinal.data.entity.TipoMovimiento
import com.example.proyectofinal.data.entity.Usuario
import com.example.proyectofinal.data.repository.CategoriaRepository
import com.example.proyectofinal.data.repository.MovimientoRepository
import com.example.proyectofinal.data.repository.UsuarioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    idUsuario: Int,
    usuarioRepository: UsuarioRepository,
    movimientoRepository: MovimientoRepository,
    categoriaRepository: CategoriaRepository,
    onNuevoMovimiento: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    // Obtenemos los flujos para saldo en tiempo real
    val saldo by movimientoRepository.calcularSaldo(idUsuario).collectAsState(initial = 0.0)
    val totalIngresos by movimientoRepository.obtenerSumaIngresos(idUsuario).collectAsState(initial = 0.0)
    val totalGastos by movimientoRepository.obtenerSumaGastos(idUsuario).collectAsState(initial = 0.0)
    val todosMovimientos by movimientoRepository.obtenerMovimientosConCategoria(idUsuario).collectAsState(initial = emptyList())

    // Estado para filtros: 0 = Todos, 1 = Ingresos, 2 = Gastos
    var filtroTipo by remember { mutableIntStateOf(0) }
    var categoriaSeleccionadaId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(idUsuario) {
        usuario = usuarioRepository.obtenerUsuarioPorId(idUsuario)
        categoriaRepository.inicializarCategoriasSiEstaVacio()
        categorias = categoriaRepository.obtenerTodasLista()
    }

    val movimientosFiltrados = remember(todosMovimientos, filtroTipo, categoriaSeleccionadaId) {
        todosMovimientos.filter { m ->
            val pasaTipo = when (filtroTipo) {
                1 -> m.tipo == TipoMovimiento.INGRESO
                2 -> m.tipo == TipoMovimiento.GASTO
                else -> true
            }
            val pasaCat = categoriaSeleccionadaId == null || m.idCategoria == categoriaSeleccionadaId
            pasaTipo && pasaCat
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.bienvenido_hola, usuario?.nombre ?: "Estudiante"),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.cerrar_sesion))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoMovimiento,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nuevo_movimiento))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Saldo Disponible en Tiempo Real
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (saldo >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.saldo_disponible),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${String.format("%.2f", saldo)} MXN",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (saldo >= 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Resumen de Ingresos y Gastos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.total_ingresos), style = MaterialTheme.typography.labelLarge, color = Color(0xFF2E7D32))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$${String.format("%.2f", totalIngresos)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.total_gastos), style = MaterialTheme.typography.labelLarge, color = Color(0xFFC62828))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$${String.format("%.2f", totalGastos)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
                        }
                    }
                }
            }

            // Sección de Filtros
            item {
                Column {
                    Text(
                        text = stringResource(R.string.movimientos_recientes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = filtroTipo == 0,
                            onClick = { filtroTipo = 0 },
                            label = { Text(stringResource(R.string.todos)) }
                        )
                        FilterChip(
                            selected = filtroTipo == 1,
                            onClick = { filtroTipo = 1 },
                            label = { Text(stringResource(R.string.ingresos)) }
                        )
                        FilterChip(
                            selected = filtroTipo == 2,
                            onClick = { filtroTipo = 2 },
                            label = { Text(stringResource(R.string.gastos)) }
                        )
                    }

                    if (categorias.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = categoriaSeleccionadaId == null,
                                    onClick = { categoriaSeleccionadaId = null },
                                    label = { Text(stringResource(R.string.filtrar_categoria)) }
                                )
                            }
                            items(categorias) { cat ->
                                FilterChip(
                                    selected = categoriaSeleccionadaId == cat.idCategoria,
                                    onClick = {
                                        categoriaSeleccionadaId = if (categoriaSeleccionadaId == cat.idCategoria) null else cat.idCategoria
                                    },
                                    label = { Text(cat.nombre) }
                                )
                            }
                        }
                    }
                }
            }

            // Lista de Movimientos
            if (movimientosFiltrados.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.sin_movimientos),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                items(movimientosFiltrados) { mov ->
                    ItemMovimiento(movimiento = mov)
                }
            }
        }
    }
}

@Composable
private fun ItemMovimiento(movimiento: MovimientoConCategoria) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val colorMonto = if (esIngreso) Color(0xFF2E7D32) else Color(0xFFC62828)
    val signo = if (esIngreso) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (esIngreso) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esIngreso) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = colorMonto
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${movimiento.nombreCategoria} • ${movimiento.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (movimiento.nota.isNotBlank()) {
                    Text(
                        text = movimiento.nota,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = "$signo$${String.format("%.2f", movimiento.monto)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorMonto
            )
        }
    }
}
