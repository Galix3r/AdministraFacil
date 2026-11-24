package com.example.proyectofinal.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.R
import com.example.proyectofinal.data.entity.Categoria
import com.example.proyectofinal.data.entity.TipoMovimiento
import com.example.proyectofinal.data.repository.CategoriaRepository
import com.example.proyectofinal.data.repository.MovimientoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoScreen(
    idUsuario: Int,
    movimientoRepository: MovimientoRepository,
    categoriaRepository: CategoriaRepository,
    onMovimientoRegistrado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var tipo by remember { mutableStateOf(TipoMovimiento.GASTO) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var expandidoMenuCategoria by remember { mutableStateOf(false) }

    var montoTexto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        categoriaRepository.inicializarCategoriasSiEstaVacio()
        val lista = categoriaRepository.obtenerTodasLista()
        categorias = lista
        if (lista.isNotEmpty()) {
            categoriaSeleccionada = lista.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nuevo_movimiento), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seleccionar tipo: INGRESO o GASTO
            Text(
                text = stringResource(R.string.tipo_movimiento),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { tipo = TipoMovimiento.INGRESO },
                    modifier = Modifier.weight(1f),
                    colors = if (tipo == TipoMovimiento.INGRESO) {
                        ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    } else ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.ingreso_tipo))
                }

                OutlinedButton(
                    onClick = { tipo = TipoMovimiento.GASTO },
                    modifier = Modifier.weight(1f),
                    colors = if (tipo == TipoMovimiento.GASTO) {
                        ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    } else ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.gasto_tipo))
                }
            }

            // Seleccionar Categoría (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expandidoMenuCategoria,
                onExpandedChange = { expandidoMenuCategoria = !expandidoMenuCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.categoria_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoMenuCategoria) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandidoMenuCategoria,
                    onDismissRequest = { expandidoMenuCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                categoriaSeleccionada = cat
                                expandidoMenuCategoria = false
                            }
                        )
                    }
                }
            }

            // Monto
            OutlinedTextField(
                value = montoTexto,
                onValueChange = { montoTexto = it },
                label = { Text(stringResource(R.string.monto_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(stringResource(R.string.descripcion_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Nota opcional
            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                label = { Text(stringResource(R.string.nota_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val montoDouble = montoTexto.toDoubleOrNull()
                    val catId = categoriaSeleccionada?.idCategoria ?: 0

                    if (montoDouble == null || montoDouble <= 0) {
                        Toast.makeText(context, "Ingresa un monto válido mayor a 0", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (descripcion.isBlank()) {
                        Toast.makeText(context, "Ingresa una descripción", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (catId <= 0) {
                        Toast.makeText(context, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    cargando = true
                    scope.launch {
                        val result = movimientoRepository.registrarMovimiento(
                            idUsuario = idUsuario,
                            idCategoria = catId,
                            tipo = tipo,
                            monto = montoDouble,
                            descripcion = descripcion,
                            nota = nota
                        )
                        cargando = false
                        result.fold(
                            onSuccess = {
                                Toast.makeText(context, "Movimiento guardado con éxito", Toast.LENGTH_SHORT).show()
                                onMovimientoRegistrado()
                            },
                            onFailure = { error ->
                                Toast.makeText(context, error.message ?: "Error al guardar", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.registrar), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
