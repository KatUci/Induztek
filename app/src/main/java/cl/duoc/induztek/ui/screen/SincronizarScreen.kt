package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.ui.PruebaViewModel
import cl.duoc.induztek.ui.codigoProtocolo

// Pantalla 10: protocolos guardados en el teléfono que faltan por subir al servidor.
// El envío a la API (Spring Boot + Retrofit) se programa en la próxima evaluación.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SincronizarScreen(
    vm: PruebaViewModel
) {
    val pendientes by vm.pendientes.collectAsStateWithLifecycle()
    var mostrarAviso by remember { mutableStateOf(false) }

    if (mostrarAviso) {
        AlertDialog(
            onDismissRequest = { mostrarAviso = false },
            confirmButton = { TextButton(onClick = { mostrarAviso = false }) { Text("OK") } },
            title = { Text("Próxima etapa") },
            text = { Text("El envío de protocolos al servidor (API REST con Spring Boot y Retrofit) se implementa en la próxima evaluación. Por ahora quedan guardados en el teléfono.") }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sincronizar") }) }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            when (pendientes.size) {
                                0 -> "Todo está al día"
                                1 -> "1 protocolo por subir"
                                else -> "${pendientes.size} protocolos por subir"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Última sincronización: todavía no se ha sincronizado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            item {
                if (pendientes.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        pendientes.forEachIndexed { i, p ->
                            if (i > 0) HorizontalDivider()
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(codigoProtocolo(p))
                                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.extraLarge) {
                                    Text(
                                        "En cola",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            item {
                Button(
                    onClick = { mostrarAviso = true },
                    enabled = pendientes.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.Sync, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Sincronizar ahora")
                }
            }
        }
    }
}
