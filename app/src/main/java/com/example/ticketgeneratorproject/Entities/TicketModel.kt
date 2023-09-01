package com.example.ticketgeneratorproject.Entities
//todo: make new data class and parser for address to properly show it in recycler view
data class TicketModel(
    val id: Int,
    val fullName: String,
    val tripNumber: String,
    val departureAddress: Address,
    val destinationAddress: Address,
    val departureTime: DateTime,
    val destinationTime: DateTime,
    val seat: Int,
    val currency: Currency,
    val purchaseTime: DateTime
)
