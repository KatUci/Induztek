package cl.duoc.induztek.ui


data class LoginFormState(
    val correo: String = "",
    val clave: String = "",
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val error: String? = null
)
