package com.levp.wolfquotes.adapters

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.levp.wolfquotes.R
import com.levp.wolfquotes.models.FavoritesEntryEntity

class FavoritesAdapter(private val favsList : ArrayList<FavoritesEntryEntity>) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>(){

    class FavoritesViewHolder (view: View):RecyclerView.ViewHolder(view){
        var entryText : TextView = view.findViewById(R.id.entryText) as TextView
        var infoText : TextView = view.findViewById(R.id.infoText) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_item,parent,false))
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.entryText.text = favsList[position].hisEntry
        holder.infoText.text = (favsList[position].hisDateTime +"\t"+ favsList[position].hisTemplate)
    }

    override fun getItemCount(): Int {
        return favsList.size
    }

    fun updateData(scanResult: ArrayList<ScanResult>) {
        favsList.clear()
        notifyDataSetChanged()
    }
}