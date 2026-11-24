package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.dao.UsuarioDao
import com.example.proyectofinal.data.entity.Usuario
import com.example.proyectofinal.data.security.PasswordUtils
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(nombre: String, correo: String, contrasena: String): Result<Long> {
        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase()

        if (nombreLimpio.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío."))
        }
        if (correoLimpio.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            return Result.failure(IllegalArgumentException("El correo electrónico no es válido."))
        }
        if (contrasena.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres."))
        }

        val usuarioExistente = usuarioDao.obtenerPorCorreo(correoLimpio)
        if (usuarioExistente != null) {
            return Result.failure(IllegalStateException("Ya existe un usuario registrado con este correo."))
        }

        val hashContrasena = PasswordUtils.hashPassword(contrasena)
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val nuevoUsuario = Usuario(
            nombre = nombreLimpio,
            correo = correoLimpio,
            contrasena = hashContrasena,
            fechaRegistro = fechaActual
        )

        return try {
            val idGenerado = usuarioDao.registrarUsuario(nuevoUsuario)
            Result.success(idGenerado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isBlank() || contrasena.isBlank()) {
            return Result.failure(IllegalArgumentException("Por favor ingresa tu correo y contraseña."))
        }

        val usuario = usuarioDao.obtenerPorCorreo(correoLimpio)
            ?: return Result.failure(IllegalArgumentException("Usuario no encontrado."))

        val esValida = PasswordUtils.verifyPassword(contrasena, usuario.contrasena)
        return if (esValida) {
            Result.success(usuario)
        } else {
            Result.failure(IllegalArgumentException("Contraseña incorrecta."))
        }
    }

    suspend fun obtenerUsuarioPorId(idUsuario: Int): Usuario? {
        return usuarioDao.obtenerPorId(idUsuario)
    }

    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodos()
    }
}
