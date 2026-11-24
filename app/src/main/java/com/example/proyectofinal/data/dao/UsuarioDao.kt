package com.example.proyectofinal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.data.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuario WHERE correo = :correo AND contrasena = :contrasenaHash LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasenaHash: String): Usuario?

    @Query("SELECT * FROM usuario WHERE id_usuario = :idUsuario LIMIT 1")
    suspend fun obtenerPorId(idUsuario: Int): Usuario?

    @Query("SELECT * FROM usuario ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Usuario>>
}
