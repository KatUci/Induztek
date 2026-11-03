package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.data.Prueba
import cl.duoc.induztek.ui.PruebaViewModel
import cl.duoc.induztek.ui.codigoProtocolo
import cl.duoc.induztek.ui.formatoDia
import cl.duoc.induztek.ui.theme.Ambar
import cl.duoc.induztek.ui.theme.Verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolosScreen(
    vm: PruebaViewModel,
    onAbrirProtocolo: (Prueba) -> Unit
) {
    val pruebas by vm.pruebas.collectAsStateWithLifecycle()
    var filtro by remember { mutableStateOf("Todos") }

    // Filtro: Todos, Pendientes (sin subir) o Subidos
    val lista = when (filtro) {
        "Pendientes" -> pruebas.filter { !it.sincronizado }
        "Subidos" -> pruebas.filter { it.sincronizado }
        else -> pruebas
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Protocolos") }) }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos", "Pendientes", "Subidos").forEach { opcion ->
                        FilterChip(
                            selected = filtro == opcion,
                            onClick = { filtro = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            item {
                if (lista.isEmpty()) {
                    Text(
                        if (pruebas.isEmpty()) "Todavía no hay protocolos. Se crean al guardar una prueba desde la ficha de un equipo."
                        else "No hay protocolos en este filtro.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // Una sola tarjeta con los protocolos separados por líneas
                    Card(Modifier.fillMaxWidth()) {
                        lista.forEachIndexed { i, p ->
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
                                    Text(codigoProtocolo(p), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${p.equipoCodigo} · ${formatoDia(p.fecha)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (p.sincronizado) Icon(Icons.Outlined.CloudDone, contentDescription = "Subido", tint = Verde)
                                else Icon(Icons.Outlined.Schedule, contentDescription = "Pendiente", tint = Ambar)
                            }
                        }
                    }
                }
            }
            item {
                Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Ambar, modifier = Modifier.size(16.dp))
                    Text(" pendiente  ·  ", style = MaterialTheme.typography.bodySmall)
                    Icon(Icons.Outlined.CloudDone, contentDescription = null, tint = Verde, modifier = Modifier.size(16.dp))
                    Text(" subido al servidor", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
