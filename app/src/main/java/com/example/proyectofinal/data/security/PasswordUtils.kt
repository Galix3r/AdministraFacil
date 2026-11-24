package com.example.proyectofinal.data.security

import java.security.MessageDigest

object PasswordUtils {
    /**
     * Convierte la contraseña en texto plano a un hash SHA-256 seguro.
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifica si una contraseña ingresada coincide con el hash almacenado.
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return hashPassword(password) == storedHash
    }
}
