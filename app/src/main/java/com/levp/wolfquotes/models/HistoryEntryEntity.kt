package com.levp.wolfquotes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.levp.wolfquotes.models.HistoryEntryEntity.Companion.HISTORY_ENTITY_TABLE

@Entity(tableName = HISTORY_ENTITY_TABLE)
data class HistoryEntryEntity (

    @PrimaryKey(autoGenerate = true)
    val hisEntryId : Int,
    val hisEntry : String,
    val hisDateTime : String,
    val hisTemplate : String
)
{
    companion object {
        const val HISTORY_ENTITY_TABLE = "history_table"
    }
}