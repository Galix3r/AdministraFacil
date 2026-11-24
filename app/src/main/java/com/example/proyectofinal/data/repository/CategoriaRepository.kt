package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.dao.CategoriaDao
import com.example.proyectofinal.data.entity.Categoria
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

    fun obtenerCategorias(): Flow<List<Categoria>> {
        return categoriaDao.obtenerTodas()
    }

    suspend fun obtenerTodasLista(): List<Categoria> {
        return categoriaDao.obtenerTodasLista()
    }

    suspend fun obtenerPorId(idCategoria: Int): Categoria? {
        return categoriaDao.obtenerPorId(idCategoria)
    }

    suspend fun insertarCategoria(categoria: Categoria): Long {
        return categoriaDao.insertar(categoria)
    }

    suspend fun inicializarCategoriasSiEstaVacio() {
        if (categoriaDao.contarCategorias() == 0) {
            val predeterminadas = listOf(
                Categoria(nombre = "Comida", descripcion = "Gastos en alimentos, restaurantes y despensa", icono = "restaurant"),
                Categoria(nombre = "Papelería", descripcion = "Útiles escolares, copias e impresiones", icono = "edit_note"),
                Categoria(nombre = "Transporte", descripcion = "Pasajes, transporte público y gasolina", icono = "directions_bus"),
                Categoria(nombre = "Educación", descripcion = "Matrículas, colegiaturas, libros y cursos", icono = "school"),
                Categoria(nombre = "Salidas", descripcion = "Entretenimiento, cine y eventos sociales", icono = "local_activity"),
                Categoria(nombre = "Otros", descripcion = "Gastos e ingresos varios e imprevistos", icono = "more_horiz")
            )
            categoriaDao.insertarTodas(predeterminadas)
        }
    }
}
