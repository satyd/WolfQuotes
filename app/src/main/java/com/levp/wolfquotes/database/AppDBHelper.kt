package com.levp.wolfquotes.database

import com.levp.wolfquotes.models.FavoritesEntryEntity
import com.levp.wolfquotes.models.HistoryEntryEntity

object AppDBHelper{
    var db : AppDatabase? = null
    var historyDao:HistoryDao? = null
    lateinit var historyList: ArrayList<HistoryEntryEntity>
    lateinit var favsList: ArrayList<FavoritesEntryEntity>

}
