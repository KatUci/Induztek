package cl.duoc.induztek.ui


data class PruebaFormState(
    val equipoId: Long? = null,
    val tipoPrueba: String = TIPOS_PRUEBA.first(),
    val atTierra: String = "",
    val btTierra: String = "",
    val atBt: String = "",
    val resultado: String = "",
    val observaciones: String = "",
    val fotoPath: String? = null,
    val errorAtTierra: String? = null,
    val errorBtTierra: String? = null,
    val errorAtBt: String? = null,
    val estado: EstadoGuardado = EstadoGuardado.LISTO,
    val error: String? = null
)

// Estados del botón "Guardar" (para la animación)
enum class EstadoGuardado { LISTO, GUARDANDO, GUARDADO }

// Opciones de las listas de la pantalla 5
val TIPOS_PRUEBA = listOf(
    "Resistencia de aislación",
    "Resistencia de devanados",
    "Relación de transformación (TTR)"
)

val RESULTADOS = listOf("Conforme", "Observación", "No conforme")
