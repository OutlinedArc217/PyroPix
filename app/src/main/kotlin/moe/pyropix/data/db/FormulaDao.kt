package moe.pyropix.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {
    @Query("SELECT * FROM formulas ORDER BY createdAt DESC")
    fun getAll(): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE isFav = 1 ORDER BY createdAt DESC")
    fun getFavs(): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE groupId = :gid ORDER BY createdAt DESC")
    fun getByGroup(gid: Long): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE latex LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    fun search(q: String): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE id = :id")
    suspend fun getById(id: Long): FormulaEntity?

    @Insert
    suspend fun insert(f: FormulaEntity): Long

    @Update
    suspend fun update(f: FormulaEntity)

    @Delete
    suspend fun delete(f: FormulaEntity)

    @Query("DELETE FROM formulas WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}
