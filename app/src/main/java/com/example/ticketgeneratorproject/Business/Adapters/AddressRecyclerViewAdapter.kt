package com.example.ticketgeneratorproject.Business.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketgeneratorproject.Data.Entities.Address
import com.example.ticketgeneratorproject.R

class AddressRecyclerViewAdapter(private var addressesList: MutableList<Address>): RecyclerView.Adapter<AddressRecyclerViewAdapter.ViewHolder>() {
    private var checkedItems = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return addressesList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = addressesList[position]
        val context = holder.text.context

        holder.text.text = currentItem.toString()
        holder.itemView.setOnClickListener {
            val id = currentItem.id
            if(id!=0){
                holder.checkBox.isChecked = !holder.checkBox.isChecked
                if(holder.checkBox.isChecked){
                    checkedItems.add(id)
                } else {
                    checkedItems.remove(id)
                }
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val checkBox = itemView.findViewById<CheckBox>(R.id.deleteCheckBox)
        val text = itemView.findViewById<TextView>(R.id.addressText)
    }

    fun getSelectedItems(): MutableList<Address> {
        val addressesToDelete = mutableListOf<Address>()

        val iterator = addressesList.iterator()
        while(iterator.hasNext()){
            val address = iterator.next()
            if(address.id in checkedItems){
                addressesToDelete.add(address)

                val position = addressesList.indexOf(address)
                iterator.remove()
                notifyItemRemoved(position)
            }
        }
        checkedItems.clear()
        return addressesToDelete
    }
}