package cl.duoc.induztek.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

// Usuario de prueba guardado en el teléfono.
// En la próxima evaluación el login se validará contra la API (Spring Boot).
private data class Usuario(val nombre: String, val clave: String)

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

    fun onCorreoChange(v: String) { _form.update { it.copy(correo = v, error = null) } }
    fun onClaveChange(v: String) { _form.update { it.copy(clave = v, error = null) } }

    fun ingresar(oAlFinal: () -> Unit = {}) {
        val f = _form.value
        val error = when {
            f.correo.isBlank() -> "Ingresa tu correo"
            !f.correo.contains("@") -> "El correo no es válido"
            f.clave.isBlank() -> "Ingresa tu contraseña"
            else -> null
        }
        if (error != null) {
            _form.update { it.copy(error = error) }
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
