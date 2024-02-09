package com.example.ticketgeneratorproject.Adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.ticketgeneratorproject.Entities.Address
import java.util.Locale


class AutoCompleteAddressAdapter(context: Context, resource: Int, addressList: List<Address>) :
    ArrayAdapter<Address?>(context, resource, addressList) {

    private var addressListFull: List<Address> = listOf()
    init {
        addressListFull = ArrayList<Address>(addressList)
    }

    override fun getFilter(): Filter {
        return addressFilter
    }

    private val addressFilter: Filter = object : Filter() {
        protected override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestions: MutableList<Address> = ArrayList()
            if (constraint.isNullOrEmpty()) {
                suggestions.addAll(addressListFull)
            } else {
                val filterPattern = constraint.toString().replace(", ", " ")
                    .lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in addressListFull) {
                    if (item.toString().lowercase().replace(", ", " ").contains(filterPattern)) {
                        suggestions.add(item)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        protected override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            addAll(results.values as List<Address>)
            notifyDataSetChanged()
        }
    }
}