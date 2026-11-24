package com.example.proyectofinal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.data.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: Categoria): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(categorias: List<Categoria>): List<Long>

    @Query("SELECT * FROM categoria ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categoria ORDER BY id_categoria ASC")
    suspend fun obtenerTodasLista(): List<Categoria>

    @Query("SELECT * FROM categoria WHERE id_categoria = :idCategoria LIMIT 1")
    suspend fun obtenerPorId(idCategoria: Int): Categoria?

    @Query("SELECT COUNT(*) FROM categoria")
    suspend fun contarCategorias(): Int
}
