package com.example.proyectofinal.data

import android.content.Context

class UsuarioPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("datos_usuario_session", Context.MODE_PRIVATE)

    fun guardarUsuarioId(idUsuario: Int) {
        prefs.edit().putInt("usuario_id", idUsuario).apply()
    }

    fun obtenerUsuarioId(): Int {
        return prefs.getInt("usuario_id", -1)
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
