package com.levp.wolfquotes.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.levp.wolfquotes.models.FavoritesEntryEntity.Companion.FAVORITES_ENTITY_TABLE
import com.levp.wolfquotes.models.HistoryEntryEntity.Companion.HISTORY_ENTITY_TABLE
import com.levp.wolfquotes.models.LogEntryEntity.Companion.LOG_ENTITY_TABLE

@Entity(tableName = LOG_ENTITY_TABLE)
data class LogEntryEntity (

    val logTemplate : Int,
    val logPoints : Int,
    val logEnabled : Boolean
)
{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var logId : Int = 0

    companion object {
        const val LOG_ENTITY_TABLE = "log_table"
    }
}