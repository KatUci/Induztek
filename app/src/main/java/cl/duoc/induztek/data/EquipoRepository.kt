package cl.duoc.induztek.data
import kotlinx.coroutines.flow.Flow

class EquipoRepository(private val dao: EquipoDao)  {

    val equipos: Flow<List<Equipo>> = dao.getAll()

    fun buscar(texto: String): Flow<List<Equipo>> = dao.buscar(texto.trim())

    suspend fun agregar(
        codigo: String,
        tipo: String,
        cliente: String,
        instalacion: String,
        marcaModelo: String?,
        numeroSerie: String?
    ) {
        require(codigo.isNotBlank()) { "El código no puede estar vacío" }
        require(tipo.isNotBlank()) { "El tipo de equipo no puede estar vacío" }
        require(cliente.isNotBlank()) { "El cliente no puede estar vacío" }
        require(instalacion.isNotBlank()) { "La instalación no puede estar vacía" }
        dao.insert(
            Equipo(
                codigo = codigo.trim().uppercase(),
                tipo = tipo.trim(),
                cliente = cliente.trim(),
                instalacion = instalacion.trim(),
                marcaModelo = marcaModelo?.trim()?.ifBlank { null },
                numeroSerie = numeroSerie?.trim()?.ifBlank { null }
            )
        )
    }

    suspend fun actualizar(
        id: Long,
        codigo: String,
        tipo: String,
        cliente: String,
        instalacion: String,
        marcaModelo: String?,
        numeroSerie: String?
    ) {
        require(id > 0) { "Id inválido" }
        require(codigo.isNotBlank()) { "El código no puede estar vacío" }
        require(tipo.isNotBlank()) { "El tipo de equipo no puede estar vacío" }
        require(cliente.isNotBlank()) { "El cliente no puede estar vacío" }
        require(instalacion.isNotBlank()) { "La instalación no puede estar vacía" }
        dao.update(
            Equipo(
                id = id,
                codigo = codigo.trim().uppercase(),
                tipo = tipo.trim(),
                cliente = cliente.trim(),
                instalacion = instalacion.trim(),
                marcaModelo = marcaModelo?.trim()?.ifBlank { null },
                numeroSerie = numeroSerie?.trim()?.ifBlank { null }
            )
        )
    }

    suspend fun eliminar(equipo: Equipo) = dao.delete(equipo)
    suspend fun obtener(id: Long) = dao.findById(id)
}
