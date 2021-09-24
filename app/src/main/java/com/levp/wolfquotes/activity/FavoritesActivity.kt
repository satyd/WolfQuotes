package com.levp.wolfquotes.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.levp.wolfquotes.R
import com.levp.wolfquotes.adapters.FavoritesAdapter
import com.levp.wolfquotes.database.AppDBHelper.favsList
import com.levp.wolfquotes.database.AppDBHelper.historyDao
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_favorites.toolbar
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.coroutines.*


class FavoritesActivity : AppCompatActivity() {

    //lateinit var historyList: ArrayList<HistoryEntryEntity>
    lateinit var favoritesAdapter: FavoritesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //historyViewModel =
        favsList = ArrayList(historyDao!!.pickAllFavorites())
        favoritesAdapter = FavoritesAdapter(favsList)

        favorites_holder.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoritesAdapter

            //history_holder.addOnItemTouchListener(RecyclerTouchListener)
        }

        val dividerItemDecoration = DividerItemDecoration(
            favorites_holder.context,
            LinearLayoutManager(applicationContext).orientation
        )

        favorites_holder.addItemDecoration(dividerItemDecoration)
    }

    override fun onResume() {
        super.onResume()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val currentBackgroundImage = prefs.getString("backgroundImage", "volk0")

        val resourceId: Int =
            resources.getIdentifier(currentBackgroundImage, "drawable", packageName)

        //Log.d("bg_image", currentBackgroundImage.toString())
        //Log.d("soundOn", isSoundOn.toString())

        favorites_holder.setBackgroundResource(resourceId)
    }

    fun updateDB() {
        CoroutineScope(Dispatchers.Default).launch {
            Log.e("DB updated", "dp updated")
            favsList = ArrayList(historyDao!!.pickAllFavorites())

        }
    }
}