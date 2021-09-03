package com.levp.wolfquotes.adapters

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.levp.wolfquotes.R
import com.levp.wolfquotes.models.HistoryEntryEntity
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter(private val historyList : ArrayList<HistoryEntryEntity>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){

    class HistoryViewHolder (view: View):RecyclerView.ViewHolder(view){
        var entryText : TextView = view.findViewById(R.id.entryText) as TextView
        var infoText : TextView = view.findViewById(R.id.infoText) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_item,parent,false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.entryText.text = historyList[position].hisEntry
        holder.infoText.text = (historyList[position].hisDateTime +"\t"+ historyList[position].hisTemplate)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateData(scanResult: ArrayList<ScanResult>) {
        historyList.clear()
        notifyDataSetChanged()
    }
}