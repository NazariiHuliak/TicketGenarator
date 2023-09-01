package com.example.ticketgeneratorproject.Entities
data class TicketModel(
    val id: Int,
    val fullName: String,
    val tripNumber: String,
    val departureAddress: Address,
    val destinationAddress: Address,
    var departureTime: DateTime,
    var destinationTime: DateTime,
    val seat: Int,
    val currency: Currency,
    var purchaseTime: DateTime
): java.io.Serializable{

}
