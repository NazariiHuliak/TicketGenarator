package com.example.ticketgeneratorproject.Entities
//todo: make new data class and parser for address to properly show it in recycler view
data class TicketModel(
    val id: Int,
    val fullName: String,
    val tripNumber: String,
    val destinationAddresses: String,
    val DepartureAddresses: String,
    val destinationTime: DateTime,
    val DepartureTime: DateTime,
    val seat: String,
    val currency: Currency,
    val purchaseDate: DateTime
)
