package com.levp.wolfquotes.database

import android.app.Application
import android.util.Log
import com.levp.wolfquotes.Jmeh
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

    fun getLogs():ArrayList<Int>{
        val entries = ArrayList(historyDao!!.logInit())


        val res = ArrayList(entries.map{it.logPoints}.toMutableList())

//        if(entries.size <= 0)
//        {
//            res = ArrayList(Jmeh.totalTemplates)
//            for(i in res.indices)
//                res[i] = 5
//        }
//        else {
//            for ((i, value) in entries.withIndex()) {
//                res[i] = value.logPoints
//            }
//        }

        return res
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