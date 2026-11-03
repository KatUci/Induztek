package cl.duoc.induztek.data
import kotlinx.coroutines.flow.Flow

class PruebaRepository(private val dao: PruebaDao, private val equipoDao: EquipoDao) {

    val pruebas: Flow<List<Prueba>> = dao.getAll()

    // Protocolos guardados en el teléfono que todavía no se suben al servidor
    val pendientes: Flow<List<Prueba>> = dao.getPendientes()

    fun pruebasDeEquipo(equipoId: Long): Flow<List<Prueba>> = dao.getByEquipo(equipoId)

    suspend fun agregar(
        equipoId: Long,
        tipoPrueba: String,
        atTierra: Double,
        btTierra: Double,
        atBt: Double,
        resultado: String,
        observaciones: String?,
        fotoPath: String?,
        tecnico: String
    ): Long {
        val equipo = equipoDao.findById(equipoId) ?: throw IllegalArgumentException("El equipo no existe")
        require(tipoPrueba.isNotBlank()) { "Selecciona el tipo de prueba" }
        require(atTierra >= 0) { "AT–tierra no puede ser negativo" }
        require(btTierra >= 0) { "BT–tierra no puede ser negativo" }
        require(atBt >= 0) { "AT–BT no puede ser negativo" }
        require(resultado.isNotBlank()) { "Selecciona el resultado de la prueba" }
        return dao.insert(
            Prueba(
                equipoId = equipoId,
                equipoCodigo = equipo.codigo,
                tipoPrueba = tipoPrueba,
                fecha = System.currentTimeMillis(),
                atTierra = atTierra,
                btTierra = btTierra,
                atBt = atBt,
                resultado = resultado,
                observaciones = observaciones?.trim()?.ifBlank { null },
                fotoPath = fotoPath,
                tecnico = tecnico
            )
        )
    }

    suspend fun anterior(equipoId: Long, tipoPrueba: String, antesDe: Long = System.currentTimeMillis()) =
        dao.findAnterior(equipoId, tipoPrueba, antesDe)

    suspend fun eliminar(prueba: Prueba) = dao.delete(prueba)
    suspend fun obtener(id: Long) = dao.findById(id)
    suspend fun obtenerEquipo(id: Long) = equipoDao.findById(id)
}
