package cl.duoc.induztek.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.duoc.induztek.ui.theme.*
import cl.duoc.induztek.util.cargarMiniatura

// Lista desplegable (Tipo de equipo, Tipo de prueba, Resultado)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSeleccion(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var abierto by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = abierto,
        onExpandedChange = { abierto = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            isError = error != null,
            supportingText = error?.let { { MensajeError(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}

// Campo de texto con validación: borde rojo, ícono y mensaje de error bajo el campo
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    error: String?,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = true,
        isError = error != null,
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            if (error != null) Icon(Icons.Filled.Error, contentDescription = "Campo con error")
        },
        supportingText = error?.let { { MensajeError(it) } },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = modifier.fillMaxWidth()
    )
}

// Mensaje de error bajo un campo, aparece con animación
@Composable
fun MensajeError(texto: String) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(visible = visible, enter = fadeIn() + expandVertically()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(14.dp).padding(end = 2.dp)
            )
            Text(texto, color = MaterialTheme.colorScheme.error)
        }
    }
}

// Etiqueta de color con el resultado: Conforme, Observación o No conforme
@Composable
fun EstadoChip(resultado: String) {
    val (fondo, texto) = when (resultado) {
        "Conforme" -> VerdeClaro to Verde
        "Observación" -> AmbarClaro to Ambar
        else -> RojoClaro to Rojo
    }
    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            resultado,
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

// Foto pequeña guardada en el teléfono
@Composable
fun FotoMiniatura(path: String, tamano: Dp = 64.dp) {
    val bitmap = remember(path) { cargarMiniatura(path) }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto de la placa",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(tamano).clip(RoundedCornerShape(8.dp))
        )
    }
}

// Un dato con su etiqueta: "Instalación" / "S/E Lampa"
@Composable
fun Dato(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}

// Mensaje centrado cuando una lista está vacía
@Composable
fun MensajeVacio(texto: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(texto, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Borde de líneas cortadas (el cuadro "Tomar" de la pantalla 5)
fun Modifier.bordeDiscontinuo(color: Color): Modifier = this.drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))),
        cornerRadius = CornerRadius(12.dp.toPx())
    )
}
