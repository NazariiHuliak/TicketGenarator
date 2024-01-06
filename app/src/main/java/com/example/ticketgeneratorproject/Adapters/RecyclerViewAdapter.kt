package com.example.ticketgeneratorproject.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.DetailedInformationAboutTicket
import com.example.ticketgeneratorproject.Entities.TicketModel
import com.example.ticketgeneratorproject.R


class RecyclerViewAdapter(private var ticketsList: MutableList<TicketModel>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
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
        val context = holder.fullName.context
        holder.fullName.text = currentItem.fullName
        holder.tripDepartureDestination.text = java.lang.StringBuilder(currentItem.departureAddress.city + " - " + currentItem.destinationAddress.city)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedInformationAboutTicket::class.java)
            intent.putExtra("recyclerViewAdapter_TO_DetailedInformationAboutTicket_ticketData", currentItem)
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fullName = itemView.findViewById<TextView>(R.id.fullName)
        val tripDepartureDestination = itemView.findViewById<TextView>(R.id.trip)
    }

    fun setFilteredList(newTicketsList: MutableList<TicketModel>) {
        ticketsList = newTicketsList
    }
}