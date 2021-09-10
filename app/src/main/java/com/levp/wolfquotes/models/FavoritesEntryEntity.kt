package com.levp.wolfquotes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.levp.wolfquotes.models.FavoritesEntryEntity.Companion.FAVORITES_ENTITY_TABLE
import com.levp.wolfquotes.models.HistoryEntryEntity.Companion.HISTORY_ENTITY_TABLE

@Entity(tableName = FAVORITES_ENTITY_TABLE)
data class FavoritesEntryEntity (

    @PrimaryKey(autoGenerate = true)
    val hisEntryId : Int,
    val hisEntry : String,
    val hisDateTime : String,
    val hisTemplate : String
)
{
    companion object {
        const val FAVORITES_ENTITY_TABLE = "favorites_table"
    }
}