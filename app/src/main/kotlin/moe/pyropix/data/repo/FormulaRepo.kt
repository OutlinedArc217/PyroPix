package moe.pyropix.data.repo

import moe.pyropix.data.db.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormulaRepo @Inject constructor(
    private val formulaDao: FormulaDao,
    private val groupDao: GroupDao
) {
    fun allFormulas(): Flow<List<FormulaEntity>> = formulaDao.getAll()
    fun favFormulas(): Flow<List<FormulaEntity>> = formulaDao.getFavs()
    fun byGroup(gid: Long): Flow<List<FormulaEntity>> = formulaDao.getByGroup(gid)
    fun search(q: String): Flow<List<FormulaEntity>> = formulaDao.search(q)
    suspend fun getById(id: Long) = formulaDao.getById(id)
    suspend fun save(f: FormulaEntity) = formulaDao.insert(f)
    suspend fun update(f: FormulaEntity) = formulaDao.update(f)
    suspend fun delete(f: FormulaEntity) = formulaDao.delete(f)
    suspend fun deleteByIds(ids: List<Long>) = formulaDao.deleteByIds(ids)

    fun allGroups(): Flow<List<GroupEntity>> = groupDao.getAll()
    suspend fun addGroup(g: GroupEntity) = groupDao.insert(g)
    suspend fun updateGroup(g: GroupEntity) = groupDao.update(g)
    suspend fun deleteGroup(g: GroupEntity) = groupDao.delete(g)
}
