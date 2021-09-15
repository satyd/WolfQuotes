package com.levp.wolfquotes.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.levp.wolfquotes.R
import com.levp.wolfquotes.adapters.HistoryAdapter
import com.levp.wolfquotes.database.AppDBHelper.historyDao
import com.levp.wolfquotes.database.AppDBHelper.historyList
import com.levp.wolfquotes.models.HistoryViewModel
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.coroutines.*


class HistoryActivity : AppCompatActivity() {

    //lateinit var historyList: ArrayList<HistoryEntryEntity>
    lateinit var historyAdapter: HistoryAdapter
    lateinit var historyViewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //historyViewModel =
        historyList = ArrayList(historyDao!!.pickAllHistory())
        historyAdapter = HistoryAdapter(historyList)

        history_holder.apply {
            layoutManager = LinearLayoutManager(applicationContext)

            adapter = historyAdapter

            //history_holder.addOnItemTouchListener(RecyclerTouchListener)
        }

        val dividerItemDecoration = DividerItemDecoration(
            history_holder.context,
            LinearLayoutManager(applicationContext).orientation
        )

        history_holder.addItemDecoration(dividerItemDecoration)
    }


    fun updateDB()
    {
        CoroutineScope(Dispatchers.Default).launch{
            Log.e("DB updated","dp updated")
            historyList =  ArrayList(historyDao!!.pickAllHistory())

        }
    }
}