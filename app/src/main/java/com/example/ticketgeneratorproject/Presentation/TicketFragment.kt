package com.example.ticketgeneratorproject.Presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ticketgeneratorproject.Data.Entities.TicketModel
import com.example.ticketgeneratorproject.databinding.TicketFragmentBinding

class TicketFragment : Fragment() {
    private var _binding: TicketFragmentBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TicketFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val ticket = arguments?.getSerializable("Ticket") as TicketModel

        setTicketDataInView(binding, ticket)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @SuppressLint("SetTextI18n")
        fun setTicketDataInView(binding: TicketFragmentBinding, ticket: TicketModel){
            if (ticket.fullName.length >= 32) {
                binding.fullName.textSize = 14f;
                if (ticket.fullName.length >= 36 && ticket.tripNumber.length >= 9) {
                    binding.tripNumber.textSize = 13f;
                }
            }
            binding.fullName.text = ticket.fullName
            binding.tripNumber.text = ticket.tripNumber
            binding.departureCity.text = ticket.departureAddress.city
            binding.departureAddress.text =
                ticket.departureAddress.street + " " +
                        ticket.departureAddress.number
            binding.departureDate.text = ticket.departureDateTime.date
            binding.departureTime.text = ticket.departureDateTime.time
            binding.destinationCity.text = ticket.destinationAddress.city
            binding.destinationAddress.text =
                ticket.destinationAddress.street + " " +
                        ticket.destinationAddress.number
            binding.destinationDate.text = ticket.destinationDateTime.date
            binding.destinationTime.text = ticket.destinationDateTime.time
            binding.price.text = ticket.price.toString()
            binding.currency.text = ticket.currency.toString()
            binding.seat.text = if (ticket.seat == -1) "При посадці" else ticket.seat.toString()
            binding.purchaseDate.text = ticket.purchaseDateTime.time + " " +
                    ticket.purchaseDateTime.date
        }
    }
}