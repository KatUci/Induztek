package cl.duoc.induztek.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.ui.LoginViewModel
import cl.duoc.induztek.util.TipoVibracion
import cl.duoc.induztek.util.vibrar

@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onIngresado: () -> Unit
) {
    val form by vm.form.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Vibra cuando hay un error en el correo, la contraseña o las credenciales
    val hayErrores = form.errorCorreo != null || form.errorClave != null || form.error != null
    LaunchedEffect(hayErrores) { if (hayErrores) vibrar(context, TipoVibracion.ERROR) }

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Induztek Protocolos", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Pruebas eléctricas en terreno",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            CampoTexto(
                etiqueta = "Correo",
                valor = form.correo,
                onCambio = vm::onCorreoChange,
                error = form.errorCorreo,
                icono = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(4.dp))
            CampoTexto(
                etiqueta = "Contraseña",
                valor = form.clave,
                onCambio = vm::onClaveChange,
                error = form.errorClave,
                icono = Icons.Outlined.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // El mensaje de credenciales incorrectas aparece con animación
            AnimatedVisibility(visible = form.error != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp).padding(end = 4.dp)
                    )
                    Text(form.error ?: "", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { vm.ingresar(onIngresado) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Ingresar") }

            Spacer(Modifier.height(16.dp))
            Text(
                "Usuario de prueba: tecnico1@induztek.test · contraseña 1234",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
