package com.example.ticketgeneratorproject

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.Entities.TicketModel

class RecyclerViewAdapter(private val ticketsList: MutableList<TicketModel>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

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
        holder.tripDepartureDestination.text = java.lang.StringBuilder(currentItem.DepartureAddresses + " - " + currentItem.destinationAddresses)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fullName = itemView.findViewById<TextView>(R.id.fullName)
        val tripDepartureDestination = itemView.findViewById<TextView>(R.id.trip)
    }
}