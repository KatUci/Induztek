package cl.duoc.induztek.ui

import cl.duoc.induztek.data.Prueba
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private val chile = Locale.forLanguageTag("es-CL")
private val formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
private val formatoDia = DateTimeFormatter.ofPattern("dd-MM-yyyy")

// 4.2 → "4,2"
fun formatoNumero(valor: Double): String = String.format(chile, "%.1f", valor)

fun formatoFecha(millis: Long): String =
    formatoFecha.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

fun formatoDia(millis: Long): String =
    formatoDia.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

// Código del protocolo, por ejemplo "PRT-2026-0007"
fun codigoProtocolo(prueba: Prueba): String {
    val anio = Instant.ofEpochMilli(prueba.fecha).atZone(ZoneId.systemDefault()).year
    return "PRT-$anio-" + prueba.id.toString().padStart(4, '0')
}

// Cuánto cambió una medición respecto de la anterior, en %
fun variacion(actual: Double, anterior: Double): Double? =
    if (anterior == 0.0) null else (actual - anterior) / anterior * 100

// La resistencia de aislación que baja 10% o más es una alerta
fun esAlerta(actual: Double, anterior: Double): Boolean =
    (variacion(actual, anterior) ?: 0.0) <= -10.0

// "Anterior 5,1 · bajó 18%"
fun textoComparacion(actual: Double, anterior: Double): String {
    val cambio = variacion(actual, anterior) ?: return "Anterior ${formatoNumero(anterior)}"
    val porcentaje = abs(cambio).roundToInt()
    return when {
        porcentaje == 0 -> "Anterior ${formatoNumero(anterior)} · sin cambio"
        cambio < 0 -> "Anterior ${formatoNumero(anterior)} · bajó $porcentaje%"
        else -> "Anterior ${formatoNumero(anterior)} · subió $porcentaje%"
    }
}

// Nombre corto del tipo de prueba para las listas: "Resistencia de aislación" → "Aislación"
fun nombreCorto(tipoPrueba: String): String = when (tipoPrueba) {
    "Resistencia de aislación" -> "Aislación"
    "Resistencia de devanados" -> "Devanados"
    "Relación de transformación (TTR)" -> "TTR"
    else -> tipoPrueba
}

// Año de una fecha: 2026
fun anio(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).year.toString()
