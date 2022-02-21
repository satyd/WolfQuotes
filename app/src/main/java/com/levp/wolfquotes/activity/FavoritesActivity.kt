package com.levp.wolfquotes.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.levp.wolfquotes.adapters.FavoritesAdapter
import com.levp.wolfquotes.database.AppDBHelper.favsList
import com.levp.wolfquotes.database.AppDBHelper.historyDao
import com.levp.wolfquotes.databinding.ActivityFavoritesBinding
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavoritesActivity : AppCompatActivity() {

    //lateinit var historyList: ArrayList<HistoryEntryEntity>
    lateinit var favoritesAdapter: FavoritesAdapter

    lateinit var binding: ActivityFavoritesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        favsList = ArrayList(historyDao!!.pickAllFavorites())
        favoritesAdapter = FavoritesAdapter(favsList)

        binding.favoritesHolder.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoritesAdapter

            //history_holder.addOnItemTouchListener(RecyclerTouchListener)
        }

        val dividerItemDecoration = DividerItemDecoration(
            favorites_holder.context,
            LinearLayoutManager(applicationContext).orientation
        )

        binding.favoritesHolder.addItemDecoration(dividerItemDecoration)
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