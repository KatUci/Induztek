package cl.duoc.induztek.ui


data class EquipoFormState(
    val id: Long? = null,
    val codigo: String = "",
    val tipo: String = "",
    val cliente: String = "",
    val instalacion: String = "",
    val marcaModelo: String = "",
    val numeroSerie: String = "",
    val errorCodigo: String? = null,
    val errorTipo: String? = null,
    val errorCliente: String? = null,
    val errorInstalacion: String? = null,
    val error: String? = null
) {
    val tieneErrores: Boolean
        get() = errorCodigo != null || errorTipo != null || errorCliente != null || errorInstalacion != null
}

// Opciones de la lista "Tipo de equipo" (pantalla 3)
val TIPOS_EQUIPO = listOf(
    "Transformador",
    "Relé de protección",
    "Celda MT",
    "Cable MT",
    "Interruptor",
    "Otro"
)
