package com.example.proyectofinal.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TipoMovimiento {
    const val INGRESO = "INGRESO"
    const val GASTO = "GASTO"
}

@Entity(
    tableName = "movimiento",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["id_usuario"]),
        Index(value = ["id_categoria"])
    ]
)
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_movimiento")
    val idMovimiento: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_categoria")
    val idCategoria: Int,

    @ColumnInfo(name = "tipo")
    val tipo: String,

    @ColumnInfo(name = "monto")
    val monto: Double,

    @ColumnInfo(name = "fecha")
    val fecha: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "nota")
    val nota: String
)

data class MovimientoConCategoria(
    @ColumnInfo(name = "id_movimiento") val idMovimiento: Int,
    @ColumnInfo(name = "id_usuario") val idUsuario: Int,
    @ColumnInfo(name = "id_categoria") val idCategoria: Int,
    @ColumnInfo(name = "tipo") val tipo: String,
    @ColumnInfo(name = "monto") val monto: Double,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "nota") val nota: String,
    @ColumnInfo(name = "nombreCategoria") val nombreCategoria: String,
    @ColumnInfo(name = "iconoCategoria") val iconoCategoria: String
)
