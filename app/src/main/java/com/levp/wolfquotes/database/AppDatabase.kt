package com.levp.wolfquotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.levp.wolfquotes.models.HistoryEntryEntity

@Database(entities = [HistoryEntryEntity::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun historyDao():HistoryDao

    companion object{
        @Volatile
        var INSTANCE: AppDatabase? = null

        const val dbName = "quotesDB"

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, dbName).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}