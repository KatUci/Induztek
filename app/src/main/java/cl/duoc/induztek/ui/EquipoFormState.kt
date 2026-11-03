package cl.duoc.induztek.ui


data class EquipoFormState(
    val id: Long? = null,
    val codigo: String = "",
    val tipo: String = "",
    val cliente: String = "",
    val instalacion: String = "",
    val marcaModelo: String = "",
    val numeroSerie: String = "",
    val error: String? = null
)

// Opciones de la lista "Tipo de equipo" (pantalla 3)
val TIPOS_EQUIPO = listOf(
    "Transformador",
    "Relé de protección",
    "Celda MT",
    "Cable MT",
    "Interruptor",
    "Otro"
)
