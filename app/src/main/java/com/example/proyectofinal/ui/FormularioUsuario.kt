package com.example.proyectofinal.ui

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.R

data class FormularioUsuarioDatos(
    val nombre: String,
    val correo: String,
    val telefono: String
)

@Composable
fun FormularioUsuario(
    inicial: FormularioUsuarioDatos?,
    obligatorio: Boolean,
    onGuardar: (FormularioUsuarioDatos) -> Unit,
    onCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var correo by remember { mutableStateOf(inicial?.correo ?: "") }
    var telefono by remember { mutableStateOf(inicial?.telefono ?: "") }
    var intento by remember { mutableStateOf(false) }

    val nombreOk = nombre.isNotBlank()
    val correoOk = Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
    val telefonoOk = telefono.length == 10

    AlertDialog(
        onDismissRequest = { if (!obligatorio) onCerrar() },
        title = { Text(stringResource(R.string.registro_titulo)) },
        text = {
            Column {
                Text(stringResource(R.string.registro_descripcion))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(stringResource(R.string.campo_nombre)) },
                    singleLine = true,
                    isError = intento && !nombreOk,
                    supportingText = { if (intento && !nombreOk) Text(stringResource(R.string.error_nombre)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text(stringResource(R.string.campo_correo)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = intento && !correoOk,
                    supportingText = { if (intento && !correoOk) Text(stringResource(R.string.error_correo)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) telefono = it },
                    label = { Text(stringResource(R.string.campo_telefono)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = intento && !telefonoOk,
                    supportingText = { if (intento && !telefonoOk) Text(stringResource(R.string.error_telefono)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                intento = true
                if (nombreOk && correoOk && telefonoOk) {
                    onGuardar(FormularioUsuarioDatos(nombre.trim(), correo.trim(), telefono))
                }
            }) { Text(stringResource(R.string.guardar)) }
        },
        dismissButton = {
            if (!obligatorio) {
                TextButton(onClick = onCerrar) { Text(stringResource(R.string.cancelar)) }
            }
        }
    )
}
