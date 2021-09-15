package com.levp.wolfquotes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.levp.wolfquotes.models.FavoritesEntryEntity
import com.levp.wolfquotes.models.HistoryEntryEntity
import com.levp.wolfquotes.models.LogEntryEntity


@Dao
interface HistoryDao {

    @Query("SELECT * FROM HISTORY_TABLE ORDER BY hisDateTime DESC")
    fun pickAllHistory(): List<HistoryEntryEntity>

    @Query("SELECT * FROM FAVORITES_TABLE ORDER BY hisDateTime DESC")
    fun pickAllFavorites(): List<FavoritesEntryEntity>

    @Query("SELECT * FROM HISTORY_TABLE ORDER BY hisDateTime DESC LIMIT :amount")
    fun pickPartHistory(amount: Int): List<HistoryEntryEntity>

    @Query("SELECT * FROM LOG_TABLE ORDER BY logId")
    fun logInit():List<LogEntryEntity>

    @Query("UPDATE LOG_TABLE SET logPoints = :value WHERE logId = :index")
    fun updLog(value: Int, index: Int)

    @Query("SELECT COUNT(logId) FROM LOG_TABLE")
    fun getLogCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entryEntity: HistoryEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun initLogs(entryEntity: LogEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavEntry(entryEntity: FavoritesEntryEntity)

}