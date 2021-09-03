package com.levp.wolfquotes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.levp.wolfquotes.models.HistoryEntry
import com.levp.wolfquotes.models.HistoryEntryEntity

@Dao
interface HistoryDao {

    @Query("SELECT * FROM HISTORY_TABLE ORDER BY hisDateTime DESC")
    fun pickAllHistory():List<HistoryEntryEntity>

    @Query("SELECT * FROM HISTORY_TABLE ORDER BY hisDateTime DESC LIMIT :amount")
    fun pickPartHistory(amount:Int):List<HistoryEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entryEntity: HistoryEntryEntity)
}