package com.example.proyectofinal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinal.data.dao.CategoriaDao
import com.example.proyectofinal.data.dao.MovimientoDao
import com.example.proyectofinal.data.dao.UsuarioDao
import com.example.proyectofinal.data.entity.Categoria
import com.example.proyectofinal.data.entity.Movimiento
import com.example.proyectofinal.data.entity.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Usuario::class, Categoria::class, Movimiento::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "administra_facil_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateCategories(database.categoriaDao())
                    }
                }
            }

            private suspend fun prepopulateCategories(categoriaDao: CategoriaDao) {
                val categoriasPredeterminadas = listOf(
                    Categoria(nombre = "Comida", descripcion = "Gastos en alimentos, restaurantes y despensa", icono = "restaurant"),
                    Categoria(nombre = "Papelería", descripcion = "Útiles escolares, copias e impresiones", icono = "edit_note"),
                    Categoria(nombre = "Transporte", descripcion = "Pasajes, transporte público y gasolina", icono = "directions_bus"),
                    Categoria(nombre = "Educación", descripcion = "Matrículas, colegiaturas, libros y cursos", icono = "school"),
                    Categoria(nombre = "Salidas", descripcion = "Entretenimiento, cine y eventos sociales", icono = "local_activity"),
                    Categoria(nombre = "Otros", descripcion = "Gastos e ingresos varios e imprevistos", icono = "more_horiz")
                )
                categoriaDao.insertarTodas(categoriasPredeterminadas)
            }
        }
    }
}
