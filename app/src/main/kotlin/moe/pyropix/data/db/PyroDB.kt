package moe.pyropix.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FormulaEntity::class, GroupEntity::class], version = 1)
abstract class PyroDB : RoomDatabase() {
    abstract fun formulaDao(): FormulaDao
    abstract fun groupDao(): GroupDao
}
