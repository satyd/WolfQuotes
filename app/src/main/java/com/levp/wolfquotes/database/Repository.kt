package com.levp.wolfquotes.database

import android.app.Application
import com.levp.wolfquotes.models.HistoryEntryEntity
import io.reactivex.Observable

class Repository(app:Application) {

    val db = AppDatabase.getAppDataBase(app.applicationContext)
    private var historyDao = db?.historyDao()
    var entryAmount:Int? = null

    fun getHistory():List<HistoryEntryEntity>{
        return historyDao!!.pickAllHistory()
    }

    fun getHistory(amount:Int):List<HistoryEntryEntity>{
        entryAmount = amount
        return historyDao!!.pickPartHistory(amount)
    }

    fun initHistory(): Observable<List<HistoryEntryEntity>> {
        return Observable.fromCallable{
            with(historyDao){
                //this?.insertEntry(entry)
                if(entryAmount != null)
                    this?.pickPartHistory(entryAmount!!)
                else this?.pickAllHistory()


            }
        }
    }
}