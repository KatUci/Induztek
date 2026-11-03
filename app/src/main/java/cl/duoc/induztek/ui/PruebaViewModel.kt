package cl.duoc.induztek.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.induztek.data.Equipo
import cl.duoc.induztek.data.Prueba
import cl.duoc.induztek.data.PruebaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Lo que muestra la pantalla del protocolo (pantalla 8)
data class ProtocoloDatos(
    val prueba: Prueba,
    val equipo: Equipo?,
    val anterior: Prueba?
)

class PruebaViewModel(private val repo: PruebaRepository): ViewModel() {

    // Todos los protocolos (pantalla 9)
    val pruebas: StateFlow<List<Prueba>> =
        repo.pruebas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Protocolos pendientes de subir (aviso de la pantalla 2 y pantalla 10)
    val pendientes: StateFlow<List<Prueba>> =
        repo.pendientes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Historial del equipo abierto (pantalla 4)
    private val _equipoId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val historial: StateFlow<List<Prueba>> =
        _equipoId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repo.pruebasDeEquipo(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Prueba anterior del mismo equipo, para comparar (pantalla 5)
    private val _anterior = MutableStateFlow<Prueba?>(null)
    val anterior: StateFlow<Prueba?> = _anterior.asStateFlow()

    // Protocolo abierto (pantalla 8)
    private val _protocolo = MutableStateFlow<ProtocoloDatos?>(null)
    val protocolo: StateFlow<ProtocoloDatos?> = _protocolo.asStateFlow()

    private val _form = MutableStateFlow(PruebaFormState())
    val form: StateFlow<PruebaFormState> = _form.asStateFlow()

    fun verHistorial(equipoId: Long) { _equipoId.value = equipoId }

    fun nuevaPrueba(equipoId: Long) {
        _form.value = PruebaFormState(equipoId = equipoId)
        cargarAnterior()
    }

    fun onTipoPruebaChange(v: String) {
        _form.update { it.copy(tipoPrueba = v) }
        cargarAnterior()
    }
    fun onAtTierraChange(v: String) { _form.update { it.copy(atTierra = v, errorAtTierra = null) } }
    fun onBtTierraChange(v: String) { _form.update { it.copy(btTierra = v, errorBtTierra = null) } }
    fun onAtBtChange(v: String) { _form.update { it.copy(atBt = v, errorAtBt = null) } }
    fun onResultadoChange(v: String) { _form.update { it.copy(resultado = v) } }
    fun onObservacionesChange(v: String) { _form.update { it.copy(observaciones = v) } }
    fun onFotoTomada(path: String) { _form.update { it.copy(fotoPath = path) } }
    fun quitarFoto() { _form.update { it.copy(fotoPath = null) } }
    fun limpiarError() { _form.update { it.copy(error = null) } }

    private fun cargarAnterior() = viewModelScope.launch {
        val f = _form.value
        val equipoId = f.equipoId ?: return@launch
        _anterior.value = repo.anterior(equipoId, f.tipoPrueba)
    }

    fun guardar(tecnico: String, oAlFinal: (Long) -> Unit = {}) = viewModelScope.launch {
        val f = _form.value

        // Validación de los 3 campos de medición
        val errorAt = errorMedicion(f.atTierra)
        val errorBt = errorMedicion(f.btTierra)
        val errorAb = errorMedicion(f.atBt)
        if (errorAt != null || errorBt != null || errorAb != null) {
            _form.update { it.copy(errorAtTierra = errorAt, errorBtTierra = errorBt, errorAtBt = errorAb) }
            return@launch
        }

        try {
            _form.update { it.copy(estado = EstadoGuardado.GUARDANDO) }
            val id = repo.agregar(
                equipoId = f.equipoId ?: throw IllegalArgumentException("Falta el equipo"),
                tipoPrueba = f.tipoPrueba,
                atTierra = leerNumero(f.atTierra)!!,
                btTierra = leerNumero(f.btTierra)!!,
                atBt = leerNumero(f.atBt)!!,
                resultado = f.resultado,
                observaciones = f.observaciones,
                fotoPath = f.fotoPath,
                tecnico = tecnico
            )
            _form.update { it.copy(estado = EstadoGuardado.GUARDADO) }
            delay(800) // deja ver la animación "Guardado"
            abrirProtocolo(id)
            _form.value = PruebaFormState()
            oAlFinal(id)
        } catch (e: Exception) {
            _form.update { it.copy(estado = EstadoGuardado.LISTO, error = e.message ?: "Error desconocido") }
        }
    }

    fun abrirProtocolo(pruebaId: Long) = viewModelScope.launch {
        _protocolo.value = null
        val prueba = repo.obtener(pruebaId) ?: return@launch
        val equipo = repo.obtenerEquipo(prueba.equipoId)
        val anterior = repo.anterior(prueba.equipoId, prueba.tipoPrueba, prueba.fecha)
        _protocolo.value = ProtocoloDatos(prueba, equipo, anterior)
    }

    fun eliminar(prueba: Prueba) = viewModelScope.launch {
        repo.eliminar(prueba)
    }

    private fun errorMedicion(texto: String): String? {
        val numero = leerNumero(texto)
        return when {
            texto.isBlank() -> "Ingresa un valor"
            numero == null -> "Número no válido"
            numero < 0 -> "No puede ser negativo"
            else -> null
        }
    }
}

// Convierte "4,2" o "4.2" en 4.2 (o null si no es un número)
fun leerNumero(texto: String): Double? = texto.trim().replace(",", ".").toDoubleOrNull()
