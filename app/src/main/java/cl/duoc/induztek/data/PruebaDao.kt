package cl.duoc.induztek.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {

    @Query("SELECT * FROM pruebas ORDER BY fecha DESC")
    fun getAll(): Flow<List<Prueba>>

    @Query("SELECT * FROM pruebas WHERE sincronizado = 0 ORDER BY fecha DESC")
    fun getPendientes(): Flow<List<Prueba>>

    @Query("SELECT * FROM pruebas WHERE equipoId = :equipoId ORDER BY fecha DESC")
    fun getByEquipo(equipoId: Long): Flow<List<Prueba>>

    @Query("SELECT * FROM pruebas WHERE equipoId = :equipoId AND tipoPrueba = :tipo AND fecha < :antesDe ORDER BY fecha DESC LIMIT 1")
    suspend fun findAnterior(equipoId: Long, tipo: String, antesDe: Long): Prueba?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prueba: Prueba): Long

    @Update
    suspend fun update(prueba: Prueba)

    @Delete
    suspend fun delete(prueba: Prueba)

    @Query("SELECT * FROM pruebas WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Prueba?
}
