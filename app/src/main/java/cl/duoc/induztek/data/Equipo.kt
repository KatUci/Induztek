package cl.duoc.induztek.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Equipo (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val tipo: String,
    val cliente: String,
    val instalacion: String,
    val marcaModelo: String? = null,
    val numeroSerie: String? = null
)