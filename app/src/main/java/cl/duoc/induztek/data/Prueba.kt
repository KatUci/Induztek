package cl.duoc.induztek.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pruebas",
    foreignKeys = [ForeignKey(
        entity = Equipo::class,
        parentColumns = ["id"],
        childColumns = ["equipoId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("equipoId")]
)
data class Prueba (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipoId: Long,
    val equipoCodigo: String,
    val tipoPrueba: String,
    val fecha: Long,
    val atTierra: Double,
    val btTierra: Double,
    val atBt: Double,
    val resultado: String,
    val observaciones: String? = null,
    val fotoPath: String? = null,
    val tecnico: String,
    val sincronizado: Boolean = false
)
