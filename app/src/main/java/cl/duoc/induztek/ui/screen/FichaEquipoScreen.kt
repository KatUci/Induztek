package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.data.Equipo
import cl.duoc.induztek.data.Prueba
import cl.duoc.induztek.ui.*
import cl.duoc.induztek.ui.theme.Ambar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FichaEquipoScreen(
    equipoVm: EquipoViewModel,
    pruebaVm: PruebaViewModel,
    onVolver: (() -> Unit)?,
    onEditar: (Equipo) -> Unit,
    onNuevaPrueba: (Equipo) -> Unit,
    onAbrirProtocolo: (Prueba) -> Unit,
    modifier: Modifier = Modifier
) {
    val equipo by equipoVm.seleccionado.collectAsStateWithLifecycle()
    val historial by pruebaVm.historial.collectAsStateWithLifecycle()
    var menu by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    // Cada vez que cambia el equipo, se carga su historial
    LaunchedEffect(equipo?.id) {
        equipo?.id?.let { pruebaVm.verHistorial(it) }
    }

    val e = equipo
    if (e == null) {
        MensajeVacio("Selecciona un equipo de la lista para ver su ficha.", modifier)
        return
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            confirmButton = {
                TextButton(onClick = {
                    equipoVm.eliminar(e)
                    confirmarEliminar = false
                    onVolver?.invoke()
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar '${e.codigo}' y todas sus pruebas?") }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(e.codigo)
                        Text(
                            "${e.tipo} · ${e.cliente}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (onVolver != null) {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    // Menú ⋮ con Editar y Eliminar
                    Box {
                        IconButton(onClick = { menu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            DropdownMenuItem(
                                text = { Text("Editar equipo") },
                                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                onClick = { menu = false; onEditar(e) }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar equipo") },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                                onClick = { menu = false; confirmarEliminar = true }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Botón azul fijo abajo, como en el mockup
            Surface(color = MaterialTheme.colorScheme.background) {
                Button(
                    onClick = { onNuevaPrueba(e) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .height(52.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Nueva prueba")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp)) {
                        Dato("Instalación", e.instalacion, Modifier.weight(1f))
                        Dato("Pruebas", "${historial.size} registradas", Modifier.weight(1f))
                    }
                }
            }

            // Comparación de la última prueba con la anterior del mismo tipo
            val ultima = historial.firstOrNull()
            val anterior = ultima?.let { u -> historial.drop(1).firstOrNull { it.tipoPrueba == u.tipoPrueba } }
            if (ultima != null) {
                item {
                    Text(
                        "Última prueba vs. anterior (GΩ)",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                    )
                    TablaComparacion(ultima, anterior, encabezadoConAnios = true)
                }
            }

            item {
                Text(
                    "Historial",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                )
                if (historial.isEmpty()) {
                    Text(
                        "Todavía no hay pruebas para este equipo. Presiona Nueva prueba.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Una sola tarjeta con las pruebas separadas por líneas
                    Card(Modifier.fillMaxWidth()) {
                        historial.forEachIndexed { i, p ->
                            if (i > 0) HorizontalDivider()
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onAbrirProtocolo(p) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(formatoDia(p.fecha), style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        nombreCorto(p.tipoPrueba),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                EstadoChip(p.resultado)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Tabla: Medición | Anterior | Actual
// En la ficha los encabezados son los años (2025 · 2026); en el protocolo dicen Anterior · Actual
@Composable
fun TablaComparacion(actual: Prueba, anterior: Prueba?, encabezadoConAnios: Boolean) {
    val filas = listOf(
        Triple("AT–tierra", anterior?.atTierra, actual.atTierra),
        Triple("BT–tierra", anterior?.btTierra, actual.btTierra),
        Triple("AT–BT", anterior?.atBt, actual.atBt)
    )
    val tituloAnterior = if (encabezadoConAnios) anterior?.let { anio(it.fecha) } ?: "—" else "Anterior"
    val tituloActual = if (encabezadoConAnios) anio(actual.fecha) else "Actual"
    val gris = MaterialTheme.colorScheme.onSurfaceVariant

    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text("Medición", Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, color = gris)
            Text(tituloAnterior, Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = gris, textAlign = TextAlign.End)
            Text(tituloActual, Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = gris, textAlign = TextAlign.End)
        }
        filas.forEach { (nombre, antes, ahora) ->
            val alerta = antes != null && esAlerta(ahora, antes)
            HorizontalDivider()
            Row(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(nombre, Modifier.weight(2f))
                Text(antes?.let { formatoNumero(it) } ?: "—", Modifier.weight(1f), color = gris, textAlign = TextAlign.End)
                Text(
                    formatoNumero(ahora) + if (alerta) " ↓" else "",
                    Modifier.weight(1f),
                    color = if (alerta) Ambar else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
