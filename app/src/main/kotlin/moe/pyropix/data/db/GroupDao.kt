package moe.pyropix.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<GroupEntity>>

    @Insert
    suspend fun insert(g: GroupEntity): Long

    @Update
    suspend fun update(g: GroupEntity)

    @Delete
    suspend fun delete(g: GroupEntity)
}
