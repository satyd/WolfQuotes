package com.levp.wolfquotes.database

import com.levp.wolfquotes.models.HistoryEntryEntity

object AppDBhelper{
    var db : AppDatabase? = null
    var historyDao:HistoryDao? = null
    lateinit var historyList: ArrayList<HistoryEntryEntity>

}
