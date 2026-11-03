package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.data.Equipo
import cl.duoc.induztek.ui.EquipoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquiposScreen(
    vm: EquipoViewModel,
    tecnico: String,
    onNuevoEquipo: () -> Unit,
    onAbrirEquipo: (Equipo) -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val equipos by vm.equipos.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    var menuPerfil by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Equipos") },
                actions = {
                    // Perfil del usuario: nombre del técnico y cerrar sesión
                    Box {
                        IconButton(onClick = { menuPerfil = true }) {
                            Icon(Icons.Outlined.AccountCircle, contentDescription = "Perfil")
                        }
                        DropdownMenu(expanded = menuPerfil, onDismissRequest = { menuPerfil = false }) {
                            Text(
                                tecnico,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                                onClick = {
                                    menuPerfil = false
                                    onCerrarSesion()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            item {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = vm::onBusquedaChange,
                    placeholder = { Text("Buscar por código o cliente") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text(
                    "Recientes",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                )
            }
            item {
                if (equipos.isEmpty()) {
                    Text(
                        if (busqueda.isBlank()) "No hay equipos. Presiona Nuevo equipo para agregar."
                        else "No se encontraron equipos.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // Una sola tarjeta con todos los equipos separados por líneas
                    Card(Modifier.fillMaxWidth()) {
                        equipos.forEachIndexed { i, e ->
                            if (i > 0) HorizontalDivider()
                            EquipoItem(equipo = e, onClick = { onAbrirEquipo(e) })
                        }
                    }
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        vm.editar(null)
                        onNuevoEquipo()
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Nuevo equipo")
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipoItem(
    equipo: Equipo,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(equipo.codigo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "${equipo.tipo} · ${equipo.instalacion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
