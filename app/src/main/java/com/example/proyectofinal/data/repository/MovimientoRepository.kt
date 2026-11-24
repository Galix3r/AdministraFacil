package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.dao.MovimientoDao
import com.example.proyectofinal.data.entity.Movimiento
import com.example.proyectofinal.data.entity.MovimientoConCategoria
import com.example.proyectofinal.data.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovimientoRepository(private val movimientoDao: MovimientoDao) {

    suspend fun registrarMovimiento(
        idUsuario: Int,
        idCategoria: Int,
        tipo: String,
        monto: Double,
        descripcion: String,
        nota: String = "",
        fecha: String = ""
    ): Result<Long> {
        if (idUsuario <= 0) {
            return Result.failure(IllegalArgumentException("ID de usuario no válido."))
        }
        if (idCategoria <= 0) {
            return Result.failure(IllegalArgumentException("Debes seleccionar una categoría válida."))
        }
        val tipoUpper = tipo.uppercase().trim()
        if (tipoUpper != TipoMovimiento.INGRESO && tipoUpper != TipoMovimiento.GASTO) {
            return Result.failure(IllegalArgumentException("El tipo de movimiento debe ser INGRESO o GASTO."))
        }
        if (monto <= 0.0) {
            return Result.failure(IllegalArgumentException("El monto debe ser una cantidad mayor a cero."))
        }
        if (descripcion.trim().isBlank()) {
            return Result.failure(IllegalArgumentException("La descripción del movimiento no puede estar vacía."))
        }

        val fechaFinal = if (fecha.isBlank()) {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        } else {
            fecha
        }

        val movimiento = Movimiento(
            idUsuario = idUsuario,
            idCategoria = idCategoria,
            tipo = tipoUpper,
            monto = monto,
            fecha = fechaFinal,
            descripcion = descripcion.trim(),
            nota = nota.trim()
        )

        return try {
            val idGenerado = movimientoDao.registrarMovimiento(movimiento)
            Result.success(idGenerado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerMovimientosUsuario(idUsuario: Int): Flow<List<Movimiento>> {
        return movimientoDao.obtenerMovimientosPorUsuario(idUsuario)
    }

    fun obtenerHistorial(idUsuario: Int): Flow<List<Movimiento>> {
        return movimientoDao.obtenerHistorialPorFecha(idUsuario)
    }

    fun obtenerPorCategoria(idUsuario: Int, idCategoria: Int): Flow<List<Movimiento>> {
        return movimientoDao.obtenerPorCategoria(idUsuario, idCategoria)
    }

    fun obtenerIngresos(idUsuario: Int): Flow<List<Movimiento>> {
        return movimientoDao.obtenerIngresos(idUsuario)
    }

    fun obtenerGastos(idUsuario: Int): Flow<List<Movimiento>> {
        return movimientoDao.obtenerGastos(idUsuario)
    }

    fun calcularSaldo(idUsuario: Int): Flow<Double> {
        return movimientoDao.calcularSaldo(idUsuario)
    }

    fun obtenerSumaIngresos(idUsuario: Int): Flow<Double> {
        return movimientoDao.obtenerSumaIngresos(idUsuario).map { it ?: 0.0 }
    }

    fun obtenerSumaGastos(idUsuario: Int): Flow<Double> {
        return movimientoDao.obtenerSumaGastos(idUsuario).map { it ?: 0.0 }
    }

    fun obtenerMovimientosConCategoria(idUsuario: Int): Flow<List<MovimientoConCategoria>> {
        return movimientoDao.obtenerMovimientosConCategoria(idUsuario)
    }
}
