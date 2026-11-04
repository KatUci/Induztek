package cl.duoc.induztek.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.induztek.data.Equipo
import cl.duoc.induztek.data.EquipoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EquipoViewModel(private val repo: EquipoRepository): ViewModel() {

    // Texto del buscador (pantalla 2)
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Lista observable desde la UI: todos los equipos o los que coinciden con la búsqueda
    @OptIn(ExperimentalCoroutinesApi::class)
    val equipos: StateFlow<List<Equipo>> =
        _busqueda.flatMapLatest { texto ->
            if (texto.isBlank()) repo.equipos else repo.buscar(texto)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Equipo abierto en la ficha (pantalla 4)
    private val _seleccionado = MutableStateFlow<Equipo?>(null)
    val seleccionado: StateFlow<Equipo?> = _seleccionado.asStateFlow()

    private val _form = MutableStateFlow(EquipoFormState())
    val form: StateFlow<EquipoFormState> = _form.asStateFlow()

    fun onBusquedaChange(v: String) { _busqueda.value = v }

    fun seleccionar(id: Long) = viewModelScope.launch {
        _seleccionado.value = repo.obtener(id)
    }

    fun editar(equipo: Equipo?) {
        _form.value = if (equipo == null) {
            EquipoFormState()
        } else {
            EquipoFormState(
                id = equipo.id,
                codigo = equipo.codigo,
                tipo = equipo.tipo,
                cliente = equipo.cliente,
                instalacion = equipo.instalacion,
                marcaModelo = equipo.marcaModelo ?: "",
                numeroSerie = equipo.numeroSerie ?: ""
            )
        }
    }

    fun onCodigoChange(v: String) { _form.update { it.copy(codigo = v, errorCodigo = null) } }
    fun onTipoChange(v: String) { _form.update { it.copy(tipo = v, errorTipo = null) } }
    fun onClienteChange(v: String) { _form.update { it.copy(cliente = v, errorCliente = null) } }
    fun onInstalacionChange(v: String) { _form.update { it.copy(instalacion = v, errorInstalacion = null) } }
    fun onMarcaModeloChange(v: String) { _form.update { it.copy(marcaModelo = v) } }
    fun onNumeroSerieChange(v: String) { _form.update { it.copy(numeroSerie = v) } }
    fun limpiarError() { _form.update { it.copy(error = null) } }

    fun guardar(oAlFinal: () -> Unit = {}) = viewModelScope.launch {
        // Validación de los campos obligatorios antes de guardar
        val validado = validar(_form.value)
        if (validado.tieneErrores) {
            _form.value = validado
            return@launch
        }

        try {
            val f = _form.value
            if (f.id == null) {
                repo.agregar(f.codigo, f.tipo, f.cliente, f.instalacion, f.marcaModelo, f.numeroSerie)
            } else {
                repo.actualizar(f.id, f.codigo, f.tipo, f.cliente, f.instalacion, f.marcaModelo, f.numeroSerie)
                _seleccionado.value = repo.obtener(f.id)
            }
            editar(null)
            oAlFinal()
        } catch (e: Exception) {
            _form.update { it.copy(error = e.message ?: "Error desconocido") }
        }
    }

    fun eliminar(equipo: Equipo) = viewModelScope.launch {
        repo.eliminar(equipo)
        if (_seleccionado.value?.id == equipo.id) _seleccionado.value = null
    }

    private fun validar(f: EquipoFormState): EquipoFormState = f.copy(
        errorCodigo = when {
            f.codigo.isBlank() -> "Ingresa el código del equipo"
            f.codigo.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        },
        errorTipo = if (f.tipo.isBlank()) "Selecciona el tipo de equipo" else null,
        errorCliente = if (f.cliente.isBlank()) "Ingresa el cliente" else null,
        errorInstalacion = if (f.instalacion.isBlank()) "Ingresa la instalación o ubicación" else null
    )
}
