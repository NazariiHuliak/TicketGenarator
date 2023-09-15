package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.Entities.TicketModel

class RecyclerViewAdapter(private var ticketsList: MutableList<TicketModel>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
    /*private var filteredList = ticketsList.toMutableList()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ticket_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ticketsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = ticketsList[position]
        holder.fullName.text = currentItem.fullName
        holder.tripDepartureDestination.text = java.lang.StringBuilder(currentItem.departureAddress.city + " - " + currentItem.destinationAddress.city)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fullName = itemView.findViewById<TextView>(R.id.fullName)
        val tripDepartureDestination = itemView.findViewById<TextView>(R.id.trip)
    }

    /*fun filter(query: String) {
        *//*Log.d("Filter", "Started")*//*
        filteredList.clear()
        *//*val filteredItems = ticketsList.filter { it.fullName.contains(query, true) }
        Log.d("myLog", "${filteredList}")
        filteredList.addAll(filteredItems)
        Log.d("myLog", "${filteredList}")*//*
        if (query.isEmpty()) {
            filteredList.addAll(ticketsList)
        } else {
            val filteredItems = ticketsList.filter { it.fullName.contains(query, true) }
            filteredList.addAll(filteredItems)
        }

        notifyDataSetChanged()
    }*/
    fun setYourDataList(newTicketsList: MutableList<TicketModel>) {
        ticketsList = newTicketsList
    }
}