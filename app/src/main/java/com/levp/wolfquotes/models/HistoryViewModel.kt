package com.levp.wolfquotes.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.levp.wolfquotes.database.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    private val historyListLiveData: MutableLiveData<List<HistoryEntryEntity>> = MutableLiveData()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            historyListLiveData.postValue(repository.getHistory())
        }

        //        CoroutineScope(Dispatchers.Default).launch{
//            historyList =  ArrayList(AppDBhelper.historyDao!!.pickAllHistory())
//        }
    }

    fun getHistory(): List<HistoryEntryEntity>? {
        return historyListLiveData.value
    }
//    class ViewModelFactory(private var repository: Repository): ViewModelProvider.Factory {
//
//        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//            return HistoryViewModel(repository) as T
//        }
//
//    }
}