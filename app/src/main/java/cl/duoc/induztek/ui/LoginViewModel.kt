package cl.duoc.induztek.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

// Usuario de prueba guardado en el teléfono.
// En la próxima evaluación el login se validará contra la API (Spring Boot).
private data class Usuario(val nombre: String, val clave: String)

private val CORREO_VALIDO = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")

class LoginViewModel : ViewModel() {

    private val usuarios = mapOf(
        "tecnico1@induztek.test" to Usuario(nombre = "Camila R.", clave = "1234"),
        "tecnico2@induztek.test" to Usuario(nombre = "Pedro S.", clave = "1234")
    )

    // Nombre del técnico que inició sesión (sale en el protocolo)
    private val _tecnico = MutableStateFlow<String?>(null)
    val tecnico: StateFlow<String?> = _tecnico.asStateFlow()

    private val _form = MutableStateFlow(LoginFormState())
    val form: StateFlow<LoginFormState> = _form.asStateFlow()

    fun onCorreoChange(v: String) { _form.update { it.copy(correo = v, errorCorreo = null, error = null) } }
    fun onClaveChange(v: String) { _form.update { it.copy(clave = v, errorClave = null, error = null) } }

    fun ingresar(oAlFinal: () -> Unit = {}) {
        val f = _form.value

        // Validación de cada campo por separado
        val errorCorreo = when {
            f.correo.isBlank() -> "Ingresa tu correo"
            !CORREO_VALIDO.matches(f.correo.trim()) -> "El correo no es válido"
            else -> null
        }
        val errorClave = when {
            f.clave.isBlank() -> "Ingresa tu contraseña"
            f.clave.length < 4 -> "Mínimo 4 caracteres"
            else -> null
        }
        if (errorCorreo != null || errorClave != null) {
            _form.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
            return
        }

        val usuario = usuarios[f.correo.trim().lowercase()]
        if (usuario == null || usuario.clave != f.clave) {
            _form.update { it.copy(error = "Correo o contraseña incorrectos") }
            return
        }

        _tecnico.value = usuario.nombre
        _form.value = LoginFormState()
        oAlFinal()
    }

    fun cerrarSesion() { _tecnico.value = null }
}
