package com.levp.wolfquotes.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.levp.wolfquotes.R
import com.levp.wolfquotes.adapters.HistoryAdapter
import com.levp.wolfquotes.models.HistoryEntryEntity
import kotlinx.android.synthetic.main.activity_history.*


class HistoryActivity : AppCompatActivity() {

    lateinit var historyList: ArrayList<HistoryEntryEntity>
    lateinit var historyAdapter: HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        historyList = ArrayList(MainActivity.db?.historyDao()!!.pickAllHistory())
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

}