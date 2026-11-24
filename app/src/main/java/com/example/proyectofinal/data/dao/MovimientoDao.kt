package com.example.proyectofinal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.data.entity.Movimiento
import com.example.proyectofinal.data.entity.MovimientoConCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarMovimiento(movimiento: Movimiento): Long

    @Query("SELECT * FROM movimiento WHERE id_usuario = :idUsuario ORDER BY fecha DESC, id_movimiento DESC")
    fun obtenerMovimientosPorUsuario(idUsuario: Int): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimiento WHERE id_usuario = :idUsuario ORDER BY fecha DESC, id_movimiento DESC")
    fun obtenerHistorialPorFecha(idUsuario: Int): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimiento WHERE id_usuario = :idUsuario AND id_categoria = :idCategoria ORDER BY fecha DESC, id_movimiento DESC")
    fun obtenerPorCategoria(idUsuario: Int, idCategoria: Int): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimiento WHERE id_usuario = :idUsuario AND tipo = 'INGRESO' ORDER BY fecha DESC, id_movimiento DESC")
    fun obtenerIngresos(idUsuario: Int): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimiento WHERE id_usuario = :idUsuario AND tipo = 'GASTO' ORDER BY fecha DESC, id_movimiento DESC")
    fun obtenerGastos(idUsuario: Int): Flow<List<Movimiento>>

    @Query("SELECT SUM(monto) FROM movimiento WHERE id_usuario = :idUsuario AND tipo = 'INGRESO'")
    fun obtenerSumaIngresos(idUsuario: Int): Flow<Double?>

    @Query("SELECT SUM(monto) FROM movimiento WHERE id_usuario = :idUsuario AND tipo = 'GASTO'")
    fun obtenerSumaGastos(idUsuario: Int): Flow<Double?>

    @Query("SELECT COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE -monto END), 0.0) FROM movimiento WHERE id_usuario = :idUsuario")
    fun calcularSaldo(idUsuario: Int): Flow<Double>

    @Query("""
        SELECT m.id_movimiento AS id_movimiento,
               m.id_usuario AS id_usuario,
               m.id_categoria AS id_categoria,
               m.tipo AS tipo,
               m.monto AS monto,
               m.fecha AS fecha,
               m.descripcion AS descripcion,
               m.nota AS nota,
               c.nombre AS nombreCategoria,
               c.icono AS iconoCategoria
        FROM movimiento m
        INNER JOIN categoria c ON m.id_categoria = c.id_categoria
        WHERE m.id_usuario = :idUsuario
        ORDER BY m.fecha DESC, m.id_movimiento DESC
    """)
    fun obtenerMovimientosConCategoria(idUsuario: Int): Flow<List<MovimientoConCategoria>>
}
