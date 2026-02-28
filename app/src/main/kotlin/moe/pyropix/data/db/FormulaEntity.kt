package moe.pyropix.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latex: String,
    val source: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFav: Boolean = false,
    val groupId: Long? = null,
    val imgPath: String? = null,
    val note: String? = null
)
