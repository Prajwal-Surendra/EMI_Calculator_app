package com.example.emi_calculator_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emi_calculator_app.R
import com.example.emi_calculator_app.modals.ListModal

class Adapter(var data: List<ListModal>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.EMI_title)
        var result = itemView.findViewById<TextView>(R.id.tvResultDisplay)
        var Sdate = itemView.findViewById<TextView>(R.id.EMI_STime)
        var Edate = itemView.findViewById<TextView>(R.id.EMI_ETime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.each_list_display,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text= data[position].EMI_title
        holder.result.text= data[position].result.toString()
        holder.Sdate.text= data[position].current_date
        holder.Edate.text= data[position].incremented_data
    }

}