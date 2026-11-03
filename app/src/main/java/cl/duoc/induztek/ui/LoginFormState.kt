package cl.duoc.induztek.ui


data class LoginFormState(
    val correo: String = "",
    val clave: String = "",
    val error: String? = null
)
